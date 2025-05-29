package attendance

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._

case class Faculty(
  name: String,
  email: String,
  courses: List[String]
)

object Faculty {
  def saveFaculty(faculty: Faculty): Unit = {
    val doc = Document(
      "name" -> faculty.name,
      "email" -> faculty.email,
      "courses" -> faculty.courses
    )
    
    Await.result(
      MongoConnection.facultyCollection.insertOne(doc).toFuture(),
      5.seconds
    )
    println(s"Saved faculty: ${faculty.name} with courses: ${faculty.courses.mkString(", ")}")
  }
  
  def getFacultyByCourse(courseCode: String): Option[Faculty] = {
    println(s"Looking up faculty for course: $courseCode")
    
    // First, let's see what's in the collection
    val allDocs = Await.result(
      MongoConnection.facultyCollection.find().toFuture(),
      5.seconds
    )
    println("All faculty in database:")
    allDocs.foreach { doc =>
      println(s"Name: ${doc.get("name").get.asString().getValue}")
      println(s"Email: ${doc.get("email").get.asString().getValue}")
      println(s"Courses: ${doc.get("courses").get.asArray().getValues.asScala.map(_.asString().getValue).mkString(", ")}")
      println("---")
    }
    
    // Now try to find the specific faculty
    val future = MongoConnection.facultyCollection
      .find(equal("courses", courseCode))  // Changed from 'in' to 'equal' for exact match
      .first()
      .toFuture()
    
    val doc = Await.result(future, 5.seconds)
    if (doc != null) {
      val faculty = Faculty(
        doc.get("name").get.asString().getValue,
        doc.get("email").get.asString().getValue,
        doc.get("courses").get.asArray().getValues.asScala.map(_.asString().getValue).toList
      )
      println(s"Found faculty: ${faculty.name} for course: $courseCode")
      Some(faculty)
    } else {
      println(s"No faculty found for course: $courseCode")
      None
    }
  }
  
  def getAllFaculty: List[Faculty] = {
    val future = MongoConnection.facultyCollection
      .find()
      .toFuture()
    
    val docs = Await.result(future, 5.seconds)
    docs.map(doc => 
      Faculty(
        doc.get("name").get.asString().getValue,
        doc.get("email").get.asString().getValue,
        doc.get("courses").get.asArray().getValues.asScala.map(_.asString().getValue).toList
      )
    ).toList
  }
} 