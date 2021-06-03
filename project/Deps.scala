import sbt._

object Versions {
  val alpakkaVersion = "3.0.0"
  val reactivemongo = "1.0.4"
}
object Deps {
  val akkaJsonStream = ("com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % Versions.alpakkaVersion)
  val akkaFile = ("com.lightbend.akka" %% "akka-stream-alpakka-file" % Versions.alpakkaVersion)
  val reactivemongo = ("org.reactivemongo" %% "reactivemongo" % Versions.reactivemongo)
  val circeBson = ("io.circe" %% "circe-bson" % "0.5.0")

  val reactivemongoBson = ("org.reactivemongo" %% "reactivemongo-bson-api" % Versions.reactivemongo)

  val scallop = "org.rogach" %% "scallop" % "4.0.3"
  val circeParser = "io.circe" %% "circe-parser" % "0.14.1"


  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime
}
