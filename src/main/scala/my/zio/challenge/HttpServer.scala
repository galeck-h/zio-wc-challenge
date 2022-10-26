package my.zio.challenge

import zhttp.http._
import zio._




object HttpServer {

    // Create HTTP route
  val app = Http.collectZIO[Request] {
    case Method.GET -> !! / "text" => ZIO.succeed(Response.text("Hello World!"))
    case Method.GET -> !! / "json" =>ZIO.succeed(Response.json("""{"greetings": "Hello World!"}"""))
    case Method.GET -> !! / "wc_stream" => for {
      service <- ZIO.service[FreqCalc]
      result <- service.calculate()
      response = Response.text(result.toString())
    } yield response
  }

  //override val run = Server.start(8080, app)
}

