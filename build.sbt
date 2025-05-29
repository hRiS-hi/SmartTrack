name := "attendance-system"
version := "1.0"
scalaVersion := "2.13.11"

scalacOptions ++= Seq("-deprecation", "-feature")

Compile / mainClass := Some("attendance.AttendanceGUI")

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.11.1",
  "com.sun.mail" % "jakarta.mail" % "2.0.1"
)

// Add JavaFX dependencies
lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
libraryDependencies ++= javaFXModules.map( m =>
  "org.openjfx" % s"javafx-$m" % "21.0.2"
) 