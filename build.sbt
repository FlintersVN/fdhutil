

val scalaV = "2.13.6"

val appVersion = "0.0.1"


lazy val common = project
  .in(file("common"))
  .settings(
    name := "common",
    version := appVersion,

    scalaVersion := scalaV
  )

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    version := appVersion,

    scalaVersion := scalaV,
    libraryDependencies ++= Seq(Deps.scallop, Deps.airframeLog)
  )
  .dependsOn(common)




lazy val cli = project
  .in(file("cli"))
  .settings(
    name := "cli",
    version := appVersion,
    packMain := Map("fdhutil" -> "io.myutilities.cli.Main"),
    scalaVersion := scalaV,
    fork := true
  )
  .dependsOn(mongoimport)
  .enablePlugins(PackPlugin)



lazy val mongoimport = project
  .in(file("mongo-import"))
  .settings(
    name := "mongo-import",
    version := appVersion,
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      Deps.akkaJsonStream,
      Deps.akkaFile,
      Deps.reactivemongo,
      Deps.circeBson,

      Deps.circeParser,
      Deps.reactivemongoBson,
      Deps.logback
    ),

  )
  .dependsOn(core)
