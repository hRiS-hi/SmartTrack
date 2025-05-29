name := "attendance-system"
version := "1.0"
scalaVersion := "2.13.11"

scalacOptions ++= Seq("-deprecation", "-feature")

Compile / mainClass := Some("AttendanceGUI")

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.7.2",
  "com.sun.mail" % "jakarta.mail" % "2.0.1",
  "org.slf4j" % "slf4j-api" % "2.0.5",
  "org.slf4j" % "slf4j-simple" % "2.0.5",
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "org.openjfx" % "javafx-controls" % "21.0.2",
  "org.openjfx" % "javafx-fxml" % "21.0.2"
) 