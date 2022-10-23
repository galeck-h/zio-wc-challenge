package my.zio.challenge

import Config._
import zio.stream.{ZPipeline, ZStream}
import zio.{Ref, Task, UIO, ZIO, ZIOAppDefault}
import WindowCalc._

import scala.collection.Seq



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
      a = calculateFrequencyWithinWindow()
    } yield ()



  def run = ???
}
