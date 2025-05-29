package attendance

import java.time.LocalDateTime
import scala.io.StdIn
import scala.util.{Try, Success, Failure}

object AttendanceSystem {
  def recordAttendance(
    courseCode: String,
    absentRollNumbers: List[String],
    submittedBy: String
  ): Unit = {
    Faculty.getFacultyByCourse(courseCode) match {
      case Some(faculty) =>
        val record = AttendanceRecord(
          courseCode = courseCode,
          facultyName = faculty.name,
          facultyEmail = faculty.email,
          date = LocalDateTime.now(),
          absentRollNumbers = absentRollNumbers,
          submittedBy = submittedBy,
          timeSlot = "" // This will be filled by the menu system
        )
        
        AttendanceRecord.saveRecord(record)
        
        val emailSubject = s"Attendance Report - $courseCode"
        val emailBody = s"""
          |Course: $courseCode
          |Date: ${AttendanceRecord.formatDateTime(record.date)}
          |Submitted by: $submittedBy
          |
          |Absent Students:
          |${absentRollNumbers.mkString("\n")}
          |""".stripMargin
        
        try {
          EmailSender.sendEmail(emailSubject, emailBody, faculty.email)
          println(s"Attendance recorded and email sent to ${faculty.name} (${faculty.email})")
        } catch {
          case e: Exception =>
            println(s"Failed to send email: ${e.getMessage}")
            println("Attendance was recorded but email could not be sent.")
        }
        
      case None =>
        println(s"No faculty found for course: $courseCode")
    }
  }

  def addFaculty(name: String, email: String, courses: List[String]): Unit = {
    val faculty = Faculty(name, email, courses)
    Faculty.saveFaculty(faculty)
    println(s"Faculty added: $name")
  }

  def getFacultyByCourse(courseCode: String): Option[Faculty] = {
    Faculty.getFacultyByCourse(courseCode)
  }

  def getAllFaculty: List[Faculty] = {
    Faculty.getAllFaculty
  }
} 