package io.myutilities.program

import org.rogach.scallop._

abstract class Program(name: String) extends Subcommand(name) {
  type Config

  def config: Config

  def run(config: Config): Unit
}
