package my.zio.challenge

import my.zio.challenge.Config.Data
import zio.{Ref, ZLayer}

object Config {

  case class Data (eventType: String, data: String, timestamp: Long)
}
//
//case class State (StateRef: Ref[Seq[Data]])
//
//object State {
//  val live = ZLayer.succeed(Ref.make(Seq.empty[Data]))
//}
