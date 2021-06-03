package io.myutilities.cli

import org.rogach.scallop._

import io.myutilities.program.Program
import io.myutilities.mongoimport.MongoImport
import wvlet.log.Logger
import wvlet.log.LogLevel

case class Conf(programs: Seq[Program], arguments: Seq[String]) extends ScallopConf(arguments) {
  val verbose = opt[Boolean](default = Some(false))
  programs.foreach(addSubcommand)
  verify()
}
object Main extends App {


  def programs: Seq[Program] = Seq(
    MongoImport
  )

  val conf = new Conf(programs, args)

  Logger.setDefaultLogLevel(if (conf.verbose()) LogLevel.ALL else LogLevel.INFO)

  val maybeExit = conf.subcommand
    .map(_.asInstanceOf[Program])
    .map(program => program.run(program.config))

  maybeExit match {
    case Some(exitCode) => sys.exit(exitCode)
    case None => sys.error("Invalid command")
  }
}

