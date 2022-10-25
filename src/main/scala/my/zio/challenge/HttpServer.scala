package my.zio.challenge

import io.circe.generic.encoding.DerivedAsObjectEncoder.deriveEncoder
import zhttp.http._
import zio._
import io.circe.syntax._




object HttpServer extends ZIOAppDefault {

    // Create HTTP route
  val app: HttpApp[Any, Nothing] = Http.collect[Request] {
    case Method.GET -> !! / "text" => Response.text("Hello World!")
    case Method.GET -> !! / "json" => Response.json("""{"greetings": "Hello World!"}""")
    case Method.GET -> !! / "wcstream" => for {
      service <- ZIO.service[LiveFreqCalc]
      result <- service.calculate()
      response <- Response.json(result.asJson)
    } yield response
  }

  //override val run = Server.start(8080, app)
}

