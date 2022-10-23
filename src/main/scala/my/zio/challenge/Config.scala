package my.zio.challenge

import zio.Ref

object Config {

  case class Data (eventType: String, data: String, timestamp: Long)

  case class State (StateRef: Ref[Seq[Data]])

}
