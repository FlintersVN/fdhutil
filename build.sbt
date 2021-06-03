

val scala3Version = "2.13.6"


val appVersion = "0.0.1"


lazy val common = project
  .in(file("common"))
  .settings(
    name := "common",
    version := appVersion,

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(Deps.scallop, Deps.logback)
  )

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    version := appVersion,

    scalaVersion := scala3Version,
    libraryDependencies += Deps.scallop
  )




lazy val cli = project
  .in(file("cli"))
  .settings(
    name := "cli",
    version := appVersion,
    scalaVersion := scala3Version,
    fork := true
  )
  .dependsOn(mongoimport)



lazy val mongoimport = project
  .in(file("mongo-import"))
  .settings(
    name := "mongo-import",
    version := appVersion,
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      Deps.akkaJsonStream,
      Deps.akkaFile,
      Deps.reactivemongo,
      Deps.circeBson,

      Deps.circeParser,
      Deps.reactivemongoBson,
    ),

  )
  .dependsOn(core)
  .dependsOn(common)
