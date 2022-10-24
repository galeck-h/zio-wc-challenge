package my.zio.challenge


import my.zio.challenge.Config.Data
import zhttp.html.body
import zhttp.http
import zhttp.http._
import zhttp.service.Server
import zio._
import my.zio.challenge.FreqCalcCalc


object HttpServer extends ZIOAppDefault {

  // Create HTTP route
  val app: HttpApp[Any, Nothing] = Http.collect[Request] {
    case Method.GET -> !! / "text" => Response.text("Hello World!")
    case Method.GET -> !! / "json" => Response.json("""{"greetings": "Hello World!"}""")
    case Method.GET -> !! / "stream" => ZIO.succeed()
  }

  //private val dataProcessingIO: ZIO[State, Throwable, Unit]

  def callFreq(windowsState:Ref[Seq[Data]]) = {
    Console.printLine("Calculating current state Freq") *>
    FreqCalcCalc.layer(windowsState).map(_)
  }

  override val run = Server.start(8080, app).provide(callFreq)
}

