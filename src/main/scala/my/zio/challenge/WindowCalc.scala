package my.zio.challenge

import my.zio.challenge.Config.Data
import zio._

import java.lang.System.{console, currentTimeMillis}
import scala.collection.MapView



object WindowCalc {

  val windowLength = 10.seconds
  var currentMaxTimestamp = Long.MinValue


  def updateWindowWithEvent(ref: Ref[Seq[Data]], newData: Data): Task[Seq[Data]] = for {
    newState <- ref.modify(state => (calcWindowState(state), calcWindowState(state)))
    _ <- Console.printLine("here")
  } yield newState


  def calcWindowState(windowSeq: Seq[Data]): Seq[Data] = {
    val currentTimestamp = currentTimeMillis() / 1000

    def predicate(ts: Long) = currentTimestamp - ts > windowLength.toSeconds

    windowSeq.filterNot(d => predicate(d.timestamp))
  }
}


object FreqCalc {
  trait Service {
    def Calculator(windowSeq: Seq[Data]): Task[MapView[String, MapView[String, Int]]]
  }

//  class CalculateFrequencyWithinWindowImpl extends FreqCalc {
//    override def Service(windowSeq: Seq[Data]): Task[MapView[String, MapView[String, Int]]] =
//      ZIO.succeed(windowSeq.groupBy(_.eventType).view.mapValues(_.groupBy(_.data).view.mapValues(_.size)))
//  }

  val live: ZLayer[Any, Nothing, Has[FreqCalc.Service]] = ZLayer.succeed(
    // that same service we wrote above
    new Service {
      override def Calculator(windowSeq: Seq[Data]): Task[MapView[String, MapView[String, Int]]] =
        ZIO.succeed(windowSeq.groupBy(_.eventType).view.mapValues(_.groupBy(_.data).view.mapValues(_.size)))
    }
  )
}