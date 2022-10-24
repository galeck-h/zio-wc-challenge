package my.zio.challenge

import my.zio.challenge.Config.Data
import zio._

import java.lang.System.{currentTimeMillis}
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


trait FreqCalc {
  def Calculate(): Task[MapView[String, MapView[String, Int]]]
}

case class LiveFreqCalc(windowsState:Ref[Seq[Data]]) extends FreqCalc {
  override def Calculate(): Task[MapView[String, MapView[String, RuntimeFlags]]] =
    for {
      state <- windowsState.get
      result <- ZIO.succeed(state.groupBy(_.eventType).view.mapValues(_.groupBy(_.data).view.mapValues(_.size)))
    } yield result
}

object FreqCalcCalc {
  def layer(windowsState: Ref[Seq[Data]]): ZLayer[Any, Nothing, LiveFreqCalc] = ZLayer { ZIO.succeed(LiveFreqCalc(windowsState)) }
}
