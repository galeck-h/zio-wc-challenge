package my.zio.challenge

import Config._
import zio.stream.{ZPipeline, ZStream}
import zio.{Ref, Task, UIO, ZIO, ZIOAppDefault, ZLayer}
import WindowCalc._

import scala.collection.{MapView, Seq}



object Main extends ZIOAppDefault {
  def putStrLn(line: String): UIO[Unit] = ZIO.succeed(() => println(line))

  def readStream : ZStream[Any, Throwable, Data] = {
    ZStream.fromInputStream(sys.process.stdin)
      .via(ZPipeline.utf8Decode)
      .map(l => l.asInstanceOf[Data])
      .map(_.copy(timestamp = System.currentTimeMillis() / 1000))
  }


  val dataWordCount =
    for {
      state <- ZIO.service[State]
      _ <- readStream.foreach(updateWindowWithEvent(state.StateRef, _))
      wc = calculateFrequencyWithinWindow(_)
    } yield wc



  def run: Task[MapView[String, MapView[String, Int]]] = dataWordCount.provide(ZLayer(Ref.make(Seq.empty[Data])).map(State)).flatMap(_)
}
