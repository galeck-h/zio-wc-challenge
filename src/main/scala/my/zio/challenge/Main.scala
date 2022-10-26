package my.zio.challenge

import Config._
import zio.stream.{ZPipeline, ZStream}
import zio.{Ref, ZIO, Task, ZIOAppDefault, ZLayer}
import my.zio.challenge.HttpServer.app
import zhttp.service.Server




object Main extends ZIOAppDefault {

  def readStream: ZStream[Any, Throwable, Data] = {
    ZStream.fromInputStream(sys.process.stdin)
      .via(ZPipeline.utf8Decode)
      //add json parsing on l
      .map(l => l.asInstanceOf[Data])
      .map(_.copy(timestamp = System.currentTimeMillis() / 1000))
      //.ignore(error)
  }

    val dataProcessor =
      for {
        freqState <- ZIO.service[FreqCalc]
        _ <- readStream.foreach(freqState.update)
      } yield ()


  def run: Task[Unit] = (for {
    _ <-  Server.start(8080, app).forkDaemon
    _ <- dataProcessor
  } yield () ).provide(FreqCalc.live, ZLayer.fromZIO(Ref.make(Seq.empty[Data])))
}
