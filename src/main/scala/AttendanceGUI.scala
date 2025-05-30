package attendance

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight}
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.stage.Stage
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.{Try, Success, Failure}

// Import our Menu object
import Menu._

object AttendanceGUI extends JFXApp3 {
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

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "SmartTrack: Attendance Management System"
      scene = createMainScene()
    }
  }

  private def createMainScene(): Scene = {
    val studentViewBtn = new Button("Student View") {
      style = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;"
      prefWidth = 200
      prefHeight = 50
      onAction = _ => showStudentView()
    }

    val crLoginBtn = new Button("CR Login") {
      style = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;"
      prefWidth = 200
      prefHeight = 50
      onAction = _ => showCRLogin()
    }

    val titleLabel = new Label("SmartTrack") {
      font = Font.font("Arial", FontWeight.Bold, 36)
      textFill = Color.DarkBlue
    }

    val subtitleLabel = new Label("Intelligent Attendance Management System") {
      font = Font.font("Arial", FontWeight.Normal, 18)
      textFill = Color.Gray
    }

    val buttonBox = new VBox(20) {
      alignment = Pos.Center
      children = Seq(studentViewBtn, crLoginBtn)
    }

    val mainLayout = new VBox(30) {
      alignment = Pos.Center
      padding = Insets(50)
      children = Seq(titleLabel, subtitleLabel, buttonBox)
      style = "-fx-background-color: white;"
    }

    new Scene(mainLayout, 800, 600)
  }

  private def showCRLogin(): Unit = {
    val dialog = new TextInputDialog() {
      title = "CR Authentication"
      headerText = "Enter CR Password"
      contentText = "Please enter the CR password:"
    }

    val result = dialog.showAndWait()
    result match {
      case Some(password) if password == CR_PASSWORD =>
        showCRMenu()
      case Some(_) =>
        new Alert(AlertType.Error) {
          title = "Authentication Failed"
          headerText = "Invalid Password"
          contentText = "The password you entered is incorrect."
        }.showAndWait()
      case None => // Dialog was cancelled
    }
  }

  private def showCRMenu(): Unit = {
    val facultyManagementBtn = new Button("Faculty Management") {
      style = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;"
      prefWidth = 200
      prefHeight = 50
      onAction = _ => showFacultyManagement()
    }

    val absenteeRecordBtn = new Button("Absentee Record") {
      style = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;"
      prefWidth = 200
      prefHeight = 50
      onAction = _ => showAbsenteeRecord()
    }

    val backBtn = new Button("Back to Main Menu") {
      style = "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;"
      prefWidth = 200
      prefHeight = 50
      onAction = _ => stage.scene = createMainScene()
    }

    val buttonBox = new VBox(20) {
      alignment = Pos.Center
      children = Seq(facultyManagementBtn, absenteeRecordBtn, backBtn)
    }

    val titleLabel = new Label("CR Menu") {
      font = Font.font("Arial", FontWeight.Bold, 36)
      textFill = Color.DarkBlue
    }

    val mainLayout = new VBox(30) {
      alignment = Pos.Center
      padding = Insets(50)
      children = Seq(titleLabel, buttonBox)
      style = "-fx-background-color: white;"
    }

    stage.scene = new Scene(mainLayout, 800, 600)
  }

  private def showStudentView(): Unit = {
    val studentStage = new Stage {
      title = "Student View"
      scene = new Scene(400, 300) {
        val layout = new VBox {
          spacing = 10
          padding = Insets(20)
          alignment = Pos.Center
          
          val rollLabel = new Label("Enter Roll Number:")
          val rollField = new TextField()
          val resultArea = new TextArea {
            editable = false
            prefRowCount = 10
          }
          
          val checkBtn = new Button("Check Attendance") {
            onAction = (e: ActionEvent) => {
              val rollInput = rollField.text.value
              val rollTry = Try(rollInput.trim.toInt)
              
              rollTry match {
                case Success(rollNumber) =>
                  
                  val records = AttendanceRecord.getRecordsByRollNumber(rollNumber.toString)
                  val absences = records.filter(_.absentRollNumbers.contains(rollNumber.toString))
                  
                  if (absences.isEmpty) {
                    resultArea.text = s"Roll number $rollNumber is not marked absent in any course."
                  } else {
                    val result = new StringBuilder
                    result.append(s"Roll number $rollNumber is absent for the following courses:\n\n")
                    absences.foreach { record =>

                      val dateTime = AttendanceRecord.formatDateTime(record.date)
                      val date = dateTime.split(" ")(0)
                     
                      result.append(s"Course: ${record.courseCode}\n")
                      result.append(s"DATE: $date\n")
                      result.append(s"Time-Slot: ${record.timeSlot}\n")
                      result.append("-------------------\n")
                    }
                    resultArea.text = result.toString()
                  }
                case Failure(_) =>
                  resultArea.text = "Invalid roll number! Please enter a valid integer."
              }
            }
          }
          
          children = Seq(rollLabel, rollField, checkBtn, resultArea)
        }
        
        root = layout
      }
    }
    
    studentStage.show()
  }
  
  private def showFacultyManagement(): Unit = {
    val facultyStage = new Stage {
      title = "Faculty Management"
      scene = new Scene(600, 500) {
        val layout = new VBox {
          spacing = 10
          padding = Insets(20)
          alignment = Pos.Center
          
          // Add Faculty Section
          val addFacultyBox = new VBox {
            spacing = 10
            padding = Insets(10)
            
            val nameLabel = new Label("Faculty Name:")
            val nameField = new TextField()
            
            val emailLabel = new Label("Email:")
            val emailField = new TextField()
            
            val coursesLabel = new Label("Courses (comma-separated):")
            val coursesField = new TextField()
            
            val addBtn = new Button("Add Faculty") {
              onAction = (e: ActionEvent) => {
                val faculty = Faculty(
                  name = nameField.text.value,
                  email = emailField.text.value,
                  courses = coursesField.text.value.split(",").map(_.trim).toList
                )
                
                Faculty.saveFaculty(faculty)
                
                new Alert(Alert.AlertType.Information) {
                  title = "Success"
                  headerText = "Faculty Added"
                  contentText = "Faculty added successfully!"
                }.showAndWait()
                
                // Clear fields
                nameField.text = ""
                emailField.text = ""
                coursesField.text = ""
              }
            }
            
            children = Seq(nameLabel, nameField, emailLabel, emailField, coursesLabel, coursesField, addBtn)
          }
          
          // View Faculty Section
          val viewFacultyBox = new VBox {
            spacing = 10
            padding = Insets(10)
            
            val facultyList = new TextArea {
              editable = false
              prefRowCount = 10
            }
            
            val refreshBtn = new Button("Refresh List") {
              onAction = (e: ActionEvent) => {
                val allFaculty = Faculty.getAllFaculty
                if (allFaculty.isEmpty) {
                  facultyList.text = "No faculty members found."
                } else {
                  val result = new StringBuilder
                  allFaculty.foreach { f =>
                    result.append(s"Name: ${f.name}\n")
                    result.append(s"Email: ${f.email}\n")
                    result.append(s"Courses: ${f.courses.mkString(", ")}\n")
                    result.append("-------------------\n")
                  }
                  facultyList.text = result.toString()
                }
              }
            }
            
            children = Seq(facultyList, refreshBtn)
          }
          
          children = Seq(
            new Label("Add New Faculty") { style = "-fx-font-weight: bold;" },
            addFacultyBox,
            new Label("View All Faculty") { style = "-fx-font-weight: bold;" },
            viewFacultyBox
          )
        }
        
        root = layout
      }
    }
    
    facultyStage.show()
  }
  
  private def showAbsenteeRecord(): Unit = {
    val crStage = new Stage {
      title = "Absentee Record"
      scene = new Scene(500, 400) {
        val layout = new VBox {
          spacing = 10
          padding = Insets(20)
          alignment = Pos.Center
          
          // Course details
          val courseLabel = new Label("Course Code:")
          val courseField = new TextField()
          
          val slotLabel = new Label("Time Slot:")
          val slotCombo = new ComboBox[String] {
            items = ObservableBuffer.from(timeSlots.map { case (_, time) => time })
          }
          
          val absentLabel = new Label("Absent Roll Numbers (comma-separated):")
          val absentField = new TextField()
          
          val submitBtn = new Button("Submit Attendance") {
            onAction = (e: ActionEvent) => {
              val courseCode = courseField.text.value
              val absentees = absentField.text.value.split(",").map(_.trim).toList
              val timeSlot = slotCombo.value.value
              
              if (absentees.nonEmpty) {
                Faculty.getFacultyByCourse(courseCode) match {
                  case Some(faculty) =>
                    val now = LocalDateTime.now()
                    val record = AttendanceRecord(
                      courseCode = courseCode,
                      facultyName = faculty.name,
                      facultyEmail = faculty.email,
                      date = now,
                      absentRollNumbers = absentees,
                      submittedBy = "CR",
                      timeSlot = timeSlot
                    )
                    
                    AttendanceRecord.saveRecord(record)
                    
                    val emailSubject = s"Attendance Report - $courseCode"
                    val emailBody = s"""
                      |Course: $courseCode
                      |Date: ${AttendanceRecord.formatDateTime(now)}
                      |Time Slot: $timeSlot
                      |Class: AI-B
                      |Submitted by: CR
                      |
                      |Absent Students:
                      |${absentees.mkString("\n")}
                      |""".stripMargin
                    
                    try {
                      EmailSender.sendEmail(emailSubject, emailBody, faculty.email)
                      new Alert(Alert.AlertType.Information) {
                        title = "Success"
                        headerText = "Attendance Recorded"
                        contentText = s"Attendance recorded and email sent to ${faculty.name}"
                      }.showAndWait()
                    } catch {
                      case e: Exception =>
                        // Still show success for attendance recording
                        new Alert(Alert.AlertType.Warning) {
                          title = "Partial Success"
                          headerText = "Attendance Recorded"
                          contentText = s"Attendance was recorded successfully, but email could not be sent.\nError: ${e.getMessage}"
                        }.showAndWait()
                    }
                    
                  case None =>
                    new Alert(Alert.AlertType.Error) {
                      title = "Error"
                      headerText = "Faculty Not Found"
                      contentText = s"No faculty found for course: $courseCode"
                    }.showAndWait()
                }
              } else {
                new Alert(Alert.AlertType.Warning) {
                  title = "Warning"
                  headerText = "No Absentees"
                  contentText = "No absentees provided. Record not saved."
                }.showAndWait()
              }
            }
          }
          
          children = Seq(
            courseLabel, courseField,
            slotLabel, slotCombo,
            absentLabel, absentField,
            submitBtn
          )
        }
        
        root = layout
      }
    }
    
    crStage.show()
  }
} 