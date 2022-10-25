package my.zio.challenge

import Config._
import zio.stream.{ZPipeline, ZStream}
import zio.{Ref, Task, UIO, ZIO, ZIOAppDefault, ZLayer}
import WindowCalc._
import my.zio.challenge.HttpServer.app
import zhttp.service.Server




object Main extends ZIOAppDefault {
  def putStrLn(line: String): UIO[Unit] = ZIO.succeed(() => println(line))

  def readStream: ZStream[Any, Throwable, Data] = {
    ZStream.fromInputStream(sys.process.stdin)
      .via(ZPipeline.utf8Decode)
      .map(l => l.asInstanceOf[Data])
      .map(_.copy(timestamp = System.currentTimeMillis() / 1000))
  }

    val dataProcessor =
      for {
        state <- Ref.make(Seq.empty[Data])
        _ <- readStream.foreach(updateWindowWithEvent(state, _))
      } yield ()


  val providedLayer= ZLayer(Ref.make(Seq.empty[Data])) ++ FreqCalcCalc.layer(Ref.make(Seq.empty[Data]))
  def run: Task[Unit] = dataProcessor.race(Server.start(8080, app)).provide(providedLayer)
}
