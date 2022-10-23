package my.zio.challenge


import zhttp.http._
import zhttp.service.Server

import zio._

object HttpServer extends ZIOAppDefault {

  // Create HTTP route
  val app: HttpApp[Any, Nothing] = Http.collect[Request] {
    case Method.GET -> !! / "text" => Response.text("Hello World!")
    case Method.GET -> !! / "json" => Response.json("""{"greetings": "Hello World!"}""")
  }

  //private val dataProcessingIO: ZIO[State, Throwable, Unit]

  override val run = Server.start(8080, app)
}

