package attendance

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Sorts._
import scala.concurrent.Await
import scala.concurrent.duration._

object MongoConnection {
  private val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017")
  private val database: MongoDatabase = mongoClient.getDatabase("attendance_system")
  
  // Collections
  val attendanceCollection: MongoCollection[Document] = database.getCollection("attendance")
  val facultyCollection: MongoCollection[Document] = database.getCollection("faculty")
  
  def closeConnection(): Unit = {
    mongoClient.close()
  }
} 