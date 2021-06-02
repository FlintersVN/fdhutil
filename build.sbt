

val scala3Version = "3.0.0"


val appVersion = "0.0.1"


lazy val common = project
  .in(file("common"))
  .settings(
    name := "common",
    version := appVersion,

    scalaVersion := scala3Version,

    libraryDependencies += Deps.scallop
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
  )
  .dependsOn(mongoimport)



lazy val mongoimport = project
  .in(file("mongo-import"))
  .settings(
    name := "mongo-import",
    version := appVersion,
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      Deps.akkaJsonStream, Deps.akkaFile, Deps.reactivemongo
    )
    .map(_.cross(CrossVersion.for3Use2_13)),

  )
  .dependsOn(core)
  .dependsOn(common)
