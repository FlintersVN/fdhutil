package io.myutilities.mongoimport


import org.rogach.scallop._

import io.myutilities.program.Program
import reactivemongo.core.protocol.Op

final case class Opt(
  db: String,
  host: String,
  port: Int,
  collection: String,
  drop: Boolean
)


object MongoImport extends Program("mongo-import") {
  type Config = Opt


  val db = opt[String](required = true)
  val host = opt[String]()
  val port = opt[Int]()
  val collection = opt[String](required = true)
  val drop = opt[Boolean]()


  def config: Config = Opt(
    db = db(),
    host = host.getOrElse("localhost"),
    port = port.getOrElse(27017),
    collection = collection(),
    drop = drop.getOrElse(false)
  )

  def run(config: Config) = {
    println(s"running with config ${config}")
  }

}