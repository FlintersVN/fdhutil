package io.myutilities.cli

import org.rogach.scallop._

import io.myutilities.program.Program
import io.myutilities.mongoimport.MongoImport

case class Conf(programs: Seq[Program], arguments: Seq[String]) extends ScallopConf(arguments) {
  programs.foreach(addSubcommand)
  verify()
}
object Main extends App {


  def programs: Seq[Program] = Seq(
    MongoImport
  )

  val conf = new Conf(programs, args)
  val maybeExit = conf.subcommand
    .map(_.asInstanceOf[Program])
    .map(program => program.run(program.config))

  maybeExit match {
    case Some(exitCode) => sys.exit(exitCode)
    case None => sys.error("Invalid command")
  }
}

