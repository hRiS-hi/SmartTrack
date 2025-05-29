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
  }
  
  def getFacultyByCourse(courseCode: String): Option[Faculty] = {
    val future = MongoConnection.facultyCollection
      .find(in("courses", courseCode))
      .first()
      .toFuture()
    
    val doc = Await.result(future, 5.seconds)
    if (doc != null) {
      Some(Faculty(
        doc.get("name").get.asString().getValue,
        doc.get("email").get.asString().getValue,
        doc.get("courses").get.asArray().getValues.asScala.map(_.asString().getValue).toList
      ))
    } else {
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