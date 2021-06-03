

val scalaV = "2.13.6"

lazy val common = project
  .in(file("common"))
  .settings(
    name := "common",
    scalaVersion := scalaV
  )

lazy val core = project
  .in(file("core"))
  .settings(
    name := "core",
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(Deps.scallop, Deps.airframeLog)
  )
  .dependsOn(common)
  .enablePlugins(GitVersioning)




lazy val cli = project
  .in(file("cli"))
  .settings(
    name := "cli",
    packMain := Map("fdhutil" -> "io.myutilities.cli.Main"),
    scalaVersion := scalaV,
    fork := true
  )
  .dependsOn(mongoimport)
  .enablePlugins(PackPlugin)
  .enablePlugins(GitVersioning)



lazy val mongoimport = project
  .in(file("mongo-import"))
  .settings(
    name := "mongo-import",
    // version := appVersion,
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
  .enablePlugins(GitVersioning)
