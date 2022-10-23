package my.zio.challenge

import my.zio.challenge.Config.Data
import zio.Console.printLine
import zio._

import java.lang.System.currentTimeMillis
import scala.collection.MapView



object WindowCalc {

  val windowLength = 10.seconds
  var currentMaxTimestamp = Long.MinValue


  def updateWindowWithEvent(ref: Ref[Seq[Data]], newData: Data): Task[Seq[Data]] = for {
    newState <- ref.modify(state => (calcWindowState(state, newData),calcWindowState(state, newData)))
  } yield newState


  def calcWindowState(windowSeq: Seq[Data], event: Data): Seq[Data] = {
    val currentTimestamp = currentTimeMillis()/ 1000
    val predicate = currentTimestamp - event.timestamp > windowLength
    windowSeq.filterNot(predicate)
  }

  def calculateFrequencyWithinWindow(windowSeq: Seq[Data]): MapView[String, MapView[String, Int]] =
    windowSeq.groupBy(_.eventType).view.mapValues(_.groupBy(_.data).view.mapValues(_.size))

}
