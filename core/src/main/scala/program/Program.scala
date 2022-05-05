package io.myutilities.program

import org.rogach.scallop._

import wvlet.log.LogSupport


abstract class Program(name: String) extends Subcommand(name) with LogSupport {
  type Config

  val verbose = opt[Boolean](default = Some(false), descr = "more detailed log output, default: false")

  def config: Config

  def run(config: Config): Int
}
