import scala.io.StdIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.{Try, Success, Failure}

object Menu {
  // CR password - in a real application, this should be stored securely
  private val CR_PASSWORD = "admin123"

  // Time slots definition as ordered list
  private val timeSlots = List(
    (1, "9:00-9:50"),
    (2, "10:00-10:50"),
    (3, "11:00-11:50"),
    (4, "12:00-12:50"),
    (5, "14:00-14:50"),
    (6, "15:00-15:50"),
    (7, "16:00-16:50")
  )

  def displayTimeSlots(): Unit = {
    println("\nAvailable Time Slots:")
    println("-------------------")
    timeSlots.foreach { case (slot, time) =>
      println(s"$slot-  $time")
    }
    println("-------------------")
  }

  def main(args: Array[String]): Unit = {
    println("\nWelcome to Attendance Management System!")
    displayTimeSlots()
    
    var running = true
    while (running) {
      println("\nMain Menu:")
      println("1. Student")
      println("2. CR")
      println("3. Faculty Management")
      println("4. Exit")
      print("Enter your choice (1-4): ")

      StdIn.readLine() match {
        case "1" => handleStudent()
        case "2" => handleCR()
        case "3" => handleFacultyManagement()
        case "4" => 
          running = false
          MongoConnection.closeConnection()
          println("Thank you for using the Attendance Management System!")
        case _ => println("Invalid choice. Please try again.")
      }
    }
  }

  def handleStudent(): Unit = {
    println("\nStudent View:")
    print("Enter your roll number: ")
    val rollInput = StdIn.readLine()
    val rollTry = Try(rollInput.trim.toInt)
    rollTry match {
      case Success(rollNumber) =>
        val today = LocalDateTime.now()
        val records = AttendanceRecord.getRecordsByDate(today)
        val absences = records.filter(_.absentRollNumbers.contains(rollNumber.toString))
        
        if (absences.isEmpty) {
          println(s"Roll number $rollNumber is not marked absent in any course today.")
        } else {
          println(s"\nRoll number $rollNumber is absent for the following courses today:")
          absences.foreach { record =>
            val dateTime = AttendanceRecord.formatDateTime(record.date)
            val date = dateTime.split(" ")(0)
            val time = dateTime.split(" ")(1)
            println(s"Course: ${record.courseCode}")
            println(s"DATE: $date")
            println(s"TIME: $time")
            println("-------------------")
          }
        }
      case Failure(_) =>
        println("Invalid roll number! Please enter a valid integer.")
    }
  }

  def handleCR(): Unit = {
    println("\nCR Authentication")
    print("Enter password: ")
    
    val password = StdIn.readLine()

    if (password == CR_PASSWORD) {
      println("\nEnter attendance details:")
      displayTimeSlots()
      print("Enter slot number (1-7): ")
      
      val slotInput = StdIn.readLine()
      val slot = Try(slotInput.trim.toInt) match {
        case Success(number) if timeSlots.exists(_._1 == number) => number
        case _ =>
          println("Invalid slot number! Please enter a number between 1 and 7.")
          return
      }
      
      print("Course code: ")
      val courseCode = StdIn.readLine()
      
      // Check if faculty exists for this course
      Faculty.getFacultyByCourse(courseCode) match {
        case Some(faculty) =>
          print("List of absentees (comma-separated roll numbers): ")
          val absenteesInput = StdIn.readLine()
          val absentees = absenteesInput.split(",")
            .map(_.trim)
            .toList

          if (absentees.nonEmpty) {
            // Get current date and time
            val now = LocalDateTime.now()
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            
            // Get time slot
            val timeSlot = timeSlots.find(_._1 == slot).map(_._2).getOrElse("")
            
            // Create attendance record
            val record = AttendanceRecord(
              courseCode = courseCode,
              facultyName = faculty.name,
              facultyEmail = faculty.email,
              date = now,
              absentRollNumbers = absentees,
              submittedBy = "CR", // Fixed value instead of asking for name
              timeSlot = timeSlot
            )
            
            // Save the record
            AttendanceRecord.saveRecord(record)
            
            // Send email notification
            val emailSubject = s"Attendance Report - $courseCode"
            val emailBody = s"""
              |Course: $courseCode
              |Date: ${now.format(dateFormatter)}
              |Time Slot: $timeSlot
              |Submitted by: CR
              |
              |Absent Students:
              |${absentees.mkString("\n")}
              |""".stripMargin
            
            EmailSender.sendEmail(emailSubject, emailBody, faculty.email)
            
            println(s"Attendance recorded and email sent to ${faculty.name} (${faculty.email})")
          } else {
            println("No absentees provided. Record not saved.")
          }
          
        case None =>
          println(s"No faculty found for course: $courseCode")
      }
    } else {
      println("Invalid password!")
    }
  }

  def handleFacultyManagement(): Unit = {
    println("\nFaculty Management")
    println("1. Add New Faculty")
    println("2. View All Faculty")
    println("3. Back to Main Menu")
    print("Enter your choice (1-3): ")

    StdIn.readLine() match {
      case "1" =>
        print("Enter faculty name: ")
        val name = StdIn.readLine()
        
        print("Enter faculty email: ")
        val email = StdIn.readLine()
        
        print("Enter courses (comma-separated): ")
        val courses = StdIn.readLine().split(",").map(_.trim).toList
        
        val faculty = Faculty(name, email, courses)
        Faculty.saveFaculty(faculty)
        println("Faculty added successfully!")
        
      case "2" =>
        val allFaculty = Faculty.getAllFaculty
        if (allFaculty.isEmpty) {
          println("No faculty members found.")
        } else {
          println("\nFaculty List:")
          println("-------------")
          allFaculty.foreach { f =>
            println(s"Name: ${f.name}")
            println(s"Email: ${f.email}")
            println(s"Courses: ${f.courses.mkString(", ")}")
            println("-------------")
          }
        }
        
      case "3" => // Back to main menu
      case _ => println("Invalid choice. Please try again.")
    }
  }
}
