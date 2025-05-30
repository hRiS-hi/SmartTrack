package attendance

import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters._

case class AttendanceRecord(
  courseCode: String,
  facultyName: String,
  facultyEmail: String,
  date: LocalDateTime,
  absentRollNumbers: List[String],
  submittedBy: String,
  timeSlot: String
)

object AttendanceRecord {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def formatDateTime(dateTime: LocalDateTime): String = {
    dateTime.format(formatter)
  }

  def saveRecord(record: AttendanceRecord): Unit = {
    val doc = Document(
      "courseCode" -> record.courseCode,
      "facultyName" -> record.facultyName,
      "facultyEmail" -> record.facultyEmail,
      "date" -> record.date.toString,
      "absentRollNumbers" -> record.absentRollNumbers,
      "submittedBy" -> record.submittedBy,
      "timeSlot" -> record.timeSlot
    )
    
    Await.result(
      MongoConnection.attendanceCollection.insertOne(doc).toFuture(),
      5.seconds
    )
  }



  def getRecordsByDate(date: LocalDateTime): List[AttendanceRecord] = {
    val dateStr = date.toString.split("T")(0) // Get just the date part
    val future = MongoConnection.attendanceCollection
      .find(regex("date", s"^$dateStr.*"))
      .toFuture()
    
    val docs = Await.result(future, 5.seconds)
    docs.map(doc => 
      AttendanceRecord(
        doc.get("courseCode").get.asString().getValue,
        doc.get("facultyName").get.asString().getValue,
        doc.get("facultyEmail").get.asString().getValue,
        LocalDateTime.parse(doc.get("date").get.asString().getValue),
        doc.get("absentRollNumbers").get.asArray().getValues.asScala.map(_.asString().getValue).toList,
        doc.get("submittedBy").get.asString().getValue,
        doc.get("timeSlot").get.asString().getValue
      )
    ).toList
  }




  def getRecordsByRollNumber(rollNumber: String): List[AttendanceRecord] = {
  val future = MongoConnection.attendanceCollection
    .find(Document("absentRollNumbers" -> rollNumber)) // MongoDB will match if rollNumber is in the array
    .toFuture()

  val docs = Await.result(future, 5.seconds)
  docs.map(doc =>
    AttendanceRecord(
      doc.get("courseCode").get.asString().getValue,
      doc.get("facultyName").get.asString().getValue,
      doc.get("facultyEmail").get.asString().getValue,
      LocalDateTime.parse(doc.get("date").get.asString().getValue),
      doc.get("absentRollNumbers").get.asArray().getValues.asScala.map(_.asString().getValue).toList,
      doc.get("submittedBy").get.asString().getValue,
      doc.get("timeSlot").get.asString().getValue
    )
  ).toList
}



} 