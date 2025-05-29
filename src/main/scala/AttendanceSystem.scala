import java.time.LocalDateTime
import scala.io.StdIn

object AttendanceSystem {
  def submitAttendance(courseCode: String, absentRollNumbers: List[String], submittedBy: String): Unit = {
    // Get faculty information for the course
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
        
        // Save the attendance record
        AttendanceRecord.saveRecord(record)
        
        // Send email to faculty
        val emailSubject = s"Attendance Report - $courseCode"
        val emailBody = s"""
          |Course: $courseCode
          |Date: ${record.date}
          |Time Slot: ${record.timeSlot}
          |Submitted by: $submittedBy
          |
          |Absent Students:
          |${absentRollNumbers.mkString("\n")}
          |""".stripMargin
        
        EmailSender.sendEmail(emailSubject, emailBody, faculty.email)
        
      case None =>
        println(s"No faculty found for course: $courseCode")
    }
  }
  
  def main(args: Array[String]): Unit = {
    println("Welcome to the Attendance Management System")
    println("1. Submit Attendance")
    println("2. Add New Faculty")
    println("3. Exit")
    
    var running = true
    while (running) {
      print("\nEnter your choice (1-3): ")
      val choice = StdIn.readInt()
      
      choice match {
        case 1 =>
          print("Enter course code: ")
          val courseCode = StdIn.readLine()
          
          print("Enter absent roll numbers (comma-separated): ")
          val absentRollNumbers = StdIn.readLine().split(",").map(_.trim).toList
          
          print("Enter your name: ")
          val submittedBy = StdIn.readLine()
          
          submitAttendance(courseCode, absentRollNumbers, submittedBy)
          println("Attendance submitted successfully!")
          
        case 2 =>
          print("Enter faculty name: ")
          val name = StdIn.readLine()
          
          print("Enter faculty email: ")
          val email = StdIn.readLine()
          
          print("Enter courses (comma-separated): ")
          val courses = StdIn.readLine().split(",").map(_.trim).toList
          
          val faculty = Faculty(name, email, courses)
          Faculty.saveFaculty(faculty)
          println("Faculty added successfully!")
          
        case 3 =>
          running = false
          MongoConnection.closeConnection()
          println("Thank you for using the Attendance Management System!")
          
        case _ =>
          println("Invalid choice. Please try again.")
      }
    }
  }
} 