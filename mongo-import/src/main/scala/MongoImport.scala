package io.myutilities.mongoimport


import org.rogach.scallop._

import java.nio.file.Path
import java.nio.file.Paths
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api.{ AsyncDriver, MongoConnection }

import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl._
import akka.stream.alpakka.json.scaladsl.JsonReader
import akka.util.ByteString
import akka.NotUsed
import akka.actor.ActorSystem


import io.circe.bson._
import io.circe._, io.circe.parser._

import reactivemongo.api.bson._
import reactivemongo.api.bson.collection._
import reactivemongo.api.{ AsyncDriver, MongoConnection }

import io.myutilities.program.Program
import scala.util.Failure
import scala.concurrent.Await
import scala.concurrent.duration.Duration






final case class Opt(
  db: String,
  host: String,
  port: Int,
  collection: String,
  username: String,
  password: String,
  drop: Boolean,
  gunzip: Boolean,
  jsonPath: String,
  directory: Path
)




object MongoImport extends Program("mongo-import") {
  type Config = Opt


  val db = opt[String](required = true)
  val host = opt[String]()
  val port = opt[Int]()
  val collection = opt[String](required = true)
  val username = opt[String](required = true)
  val password = opt[String](required = true)
  val drop = opt[Boolean]()
  val gunzip = opt[Boolean]()
  val jsonPath = opt[String]("json-path")
  val dir = opt[String](required = true).map(str => Paths.get(str))


  def config: Config = Opt(
    db = db(),
    host = host.getOrElse("localhost"),
    port = port.getOrElse(27017),
    collection = collection(),
    drop = drop.getOrElse(false),
    directory = dir(),
    username = username(),
    password = password(),
    gunzip = gunzip.getOrElse(false),
    jsonPath = jsonPath.getOrElse("$")
  )

  implicit lazy val actorSystem: ActorSystem = ActorSystem()
  lazy val logger = actorSystem.log


  def extract(config: Config) = {
    val (fileExt, flow): (String, Flow[ByteString, ByteString, NotUsed]) = if (config.gunzip)
      ".json.gz" -> Compression.gunzip()
      else ".json" -> Flow[ByteString]

    Directory.walk(config.directory)
      .filter(_.toString.endsWith(fileExt))
      .flatMapMerge(32, { p =>
        FileIO.fromPath(p)
        .via(flow)
        .via(JsonReader.select(config.jsonPath))
        .map(bs => parse(bs.utf8String))
        .wireTap { either =>
          either
            .swap
            .foreach(
              failure => logger.error(failure.underlying, s"${p} parse error: ${failure.message}")
            )
        }
        .collect {
          case Right(js) if js != Json.Null => js
        }
        .map(obj => jsonToBson(obj))
        .wireTap { either =>
          either
            .swap
            .foreach(
              failure => logger.error(failure, s"${p} convert JSON to BSON error:")
            )
        }
        .collect {
          case Right(bson) if bson.isInstanceOf[BSONDocument] => bson.asInstanceOf[BSONDocument]
        }
      })
  }

  def load(config: Config, bsonSrc: Source[BSONDocument, _]) = {
    val driver = new reactivemongo.api.AsyncDriver
    val futureCollection = driver
      .connect(s"mongodb://${config.username}:${config.password}@${config.host}:${config.port}/?readPreference=primary&ssl=false&authSource=${config.db}")
      .flatMap(_.database(config.db))
      .map(_.collection(config.collection))
    futureCollection
      .flatMap(collection => if (config.drop) collection.drop().map(_ => collection) else Future.successful(collection))
      .flatMap { collection =>
        bsonSrc.grouped(128)
          .mapAsyncUnordered(128) { objs =>
            collection.insert.many(objs)
          }
          .runWith(
            Sink.foreach { result =>
              logger.info(
                s"""ok = ${result.ok},
                n = ${result.n},
                nModified = ${result.nModified},
                upserted = ${result.upserted},
                writeErrors = ${result.writeErrors},
                writeConcernError = ${result.writeConcernError},
                code = ${result.code},
                errmsg = ${result.errmsg},
                totalN = ${result.totalN}
                """
              )
            }
          )
      }
      .andThen { _ => driver.close() }
  }


  def run(config: Config) = {
    println(s"running with config ${config}")


    val resultF = load(config, extract(config))
      .recover { throwable =>
        logger.error(throwable, "Error occurred")
        1
      }
      .map(_ => 0)


    val exitCode = Await.result(resultF, Duration.Inf)


    Await.result(actorSystem.terminate(), Duration.Inf)

    exitCode
  }

}