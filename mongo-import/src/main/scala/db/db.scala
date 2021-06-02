// package io.myutilities.mongoimport.db

// import scala.concurrent.duration._
// import scala.concurrent.ExecutionContext.Implicits.global

// import reactivemongo.api.{ AsyncDriver, MongoConnection }




// final class Db(
//   host: String,
//   port: Int,
//   username: String,
//   password: String
// ) {

//   private val driver = new reactivemongo.api.AsyncDriver

//   private val conn = driver.connect(s"mongodb://${username}:${password}@${host}:${port}/?readPreference=primary&ssl=false")




//   def close = {
//     driver.close(1.minute)
//   }


// }