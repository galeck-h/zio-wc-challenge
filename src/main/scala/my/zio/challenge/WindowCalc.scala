package my.zio.challenge

import my.zio.challenge.Config.Data
import zio.Console.printLine
import zio._

import java.lang.System.currentTimeMillis
import scala.collection.MapView

trait FreqCalc {
  def calculate(): UIO[MapView[(String, String), Int]]
  def calcWindowState(windowSeq: Seq[Data]): Seq[Data]
  def update(data: Data): UIO[Unit]
}

case class LiveFreqCalc(state:Ref[Seq[Data]]) extends FreqCalc {
  override def calculate(): UIO[MapView[(String, String), Int]] =
    state.get.map(_.groupBy(r => (r.eventType, r.data)).view.mapValues(_.size))

  override def calcWindowState(windowSeq: Seq[Data]): Seq[Data] = {
    val windowLength = 10.seconds

    val currentTimestamp = currentTimeMillis() / 1000
    def predicate(ts: Long) = currentTimestamp - ts > windowLength.toSeconds
    windowSeq.filterNot(d => predicate(d.timestamp))
  }

  override def update(data: Data): UIO[Unit] = for {
    _ <- state.modify(state => (calcWindowState(state), calcWindowState(state)))
  } yield ()
}

object FreqCalc {

  val live: ZLayer[Ref[Seq[Data]], Nothing, FreqCalc]  = ZLayer.fromZIO(ZIO.service[Ref[Seq[Data]]].flatMap(ref => { ZIO.succeed(LiveFreqCalc(ref)) }))
}
