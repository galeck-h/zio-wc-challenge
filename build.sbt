name := "zio"
version := "0.1"
scalaVersion := "2.13.8"


lazy val zioVersion = "2.0.2"
lazy val circeVersion = "0.14.3"
val zioHttpVersion = "2.0.0-RC10"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-test-junit" % zioVersion,
  "io.d11"  %% "zhttp" % zioHttpVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)


lazy val root = (project in file("."))
  .settings(
    name := "my-zio-challenge"
  )


testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")