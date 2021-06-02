package io.myutilities.cli

import org.rogach.scallop._

import io.myutilities.program.Program
import io.myutilities.mongoimport.MongoImport

def programs: Seq[Program] = Seq(
  MongoImport
)

case class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  programs.foreach(addSubcommand)
  verify()
}

@main def main(args: String*): Unit =
  val conf = new Conf(args)
  conf.subcommand
  .map(_.asInstanceOf[Program])
  .map(program => program.run(program.config))
