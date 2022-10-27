package my.zio.challenge

import Config._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import zio.stream.{ZPipeline, ZStream}
import zio.{Ref, Task, ZIO, ZIOAppDefault, ZLayer}
import my.zio.challenge.HttpServer.app
import zhttp.service.Server
import io.circe.parser._




object Main extends ZIOAppDefault {

  def readStream: ZStream[Any, Throwable, Data] = {
    ZStream.fromInputStream(sys.process.stdin)
      .via(ZPipeline.utf8Decode)
      .map(decode[Data])
      .collectRight
      .map(_.copy(timestamp = System.currentTimeMillis() / 1000))
      //.catchAllCause(_ => ZStream.succeed())
  }

    val dataProcessor: ZIO[FreqCalc, Throwable, Unit] =
      for {
        freqState <- ZIO.service[FreqCalc]
        _ <- readStream.foreach(freqState.update)
      } yield ()


  def run: Task[Unit] = (for {
    _ <-  (Server.start(8080, app) *> ZIO.never).forkDaemon
    _ <- dataProcessor
  } yield () ).provide(FreqCalc.live, ZLayer.fromZIO(Ref.make(Seq.empty[Data])))
}
