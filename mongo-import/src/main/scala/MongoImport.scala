package io.myutilities.mongoimport


import org.rogach.scallop._

import java.nio.file.Path
import java.nio.file.Paths
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.concurrent.Await
import scala.concurrent.duration.Duration


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


  val db = opt[String](required = true, descr = "database name, required")
  val host = opt[String](default = Some("localhost"), descr = "database host, default: localhost")
  val port = opt[Int](default = Some(27017), descr = "database host, default: 27017")
  val collection = opt[String](required = true, descr = "collection to be imported, required")
  val username = opt[String](required = true, descr = "database username to be imported, required")
  val password = opt[String](required = true, descr = "database password to be imported, required")
  val drop = opt[Boolean](default = Some(false),descr = "drop collection if exist, default: false")
  val gunzip = opt[Boolean](default = Some(false), descr = "gunzip files in directory, if enable only json.gz file will be processed, default: false")
  val jsonPath = opt[String]("json-path", default = Some("$"), descr = "json path to extract, default: $")
  val dir = opt[String](
    required = true,
    descr = "Absolute path, required"
  ).map(str => Paths.get(str))

  validate(dir) { d =>
    Either.cond(d.isAbsolute(), (), "<dir> must be absolute")
  }


  def config: Config = Opt(
    db = db(),
    host = host(),
    port = port(),
    collection = collection(),
    drop = drop(),
    directory = dir(),
    username = username(),
    password = password(),
    gunzip = gunzip(),
    jsonPath = jsonPath()
  )

  implicit lazy val actorSystem: ActorSystem = ActorSystem()
  // lazy val logger = actorSystem.log


  def extract(config: Config) = {
    val (fileExt, flow): (String, Flow[ByteString, ByteString, NotUsed]) = if (config.gunzip)
      ".json.gz" -> Compression.gunzip()
      else ".json" -> Flow[ByteString]

    Directory.walk(config.directory)
      .filter(_.toString.endsWith(fileExt))
      .wireTap(p => debug(s"processing ${p}"))
      .flatMapMerge(32, { p =>
        FileIO.fromPath(p)
        .via(flow)
        .via(JsonReader.select(config.jsonPath))
        .map(bs => parse(bs.utf8String))
        .wireTap { either =>
          either
            .swap
            .foreach(
              failure => error(s"${p} parse error: ${failure.message}", failure.underlying)
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
              failure => error(s"${p} convert JSON to BSON error:", failure)
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
              info(
                s"""
                | ok = ${result.ok},
                | n = ${result.n},
                | nModified = ${result.nModified},
                | upserted = ${result.upserted},
                | writeErrors = ${result.writeErrors},
                | writeConcernError = ${result.writeConcernError},
                | code = ${result.code},
                | errmsg = ${result.errmsg},
                | totalN = ${result.totalN}
                """.stripMargin
              )
            }
          )
      }
      .andThen { _ => driver.close() }
  }


  def run(config: Config) = {
    info(s"running with config ${config}")


    val resultF = load(config, extract(config))
      .recover { throwable =>
        error("Error occurred", throwable)
        1
      }
      .map(_ => 0)


    val exitCode = Await.result(resultF, Duration.Inf)


    Await.result(actorSystem.terminate(), Duration.Inf)

    exitCode
  }

}