import sbt._

object Version {
  val alpakkaVersion = "3.0.0"
}
object Deps {
  val scallop = "org.rogach" %% "scallop" % "4.0.3"
  val akkaJsonStream = "com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % Version.alpakkaVersion
  val akkaFile = "com.lightbend.akka" %% "akka-stream-alpakka-file" % Version.alpakkaVersion
  val reactivemongo = "org.reactivemongo" %% "reactivemongo" % "1.0.4"
}
