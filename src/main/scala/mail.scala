import java.util.Properties
import jakarta.mail._
import jakarta.mail.internet._

object EmailSender {

  def sendEmail(subject: String, body: String, to: String): Unit = {
    val fromEmail = "hrisheekeshgnair@gmail.com"  // Your Gmail address
    val appPassword = "yqtg rboy wuya sicp"    // Your Gmail App Password

    val props = new Properties()
    props.put("mail.smtp.host", "smtp.gmail.com")  // Gmail SMTP server
    props.put("mail.smtp.port", "587")
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.ssl.trust", "smtp.gmail.com")

    val session = Session.getInstance(props, new Authenticator() {
      override protected def getPasswordAuthentication: PasswordAuthentication =
        new PasswordAuthentication(fromEmail, appPassword)
    })

    try {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(fromEmail))
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to).asInstanceOf[Array[Address]])
      message.setSubject(subject)
      message.setText(body)

      Transport.send(message)
      println("Email sent successfully!")
    } catch {
      case e: MessagingException =>
        println(s"Failed to send email: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  def main(args: Array[String]): Unit = {
    sendEmail("Scala Email Subject", "Hello from Scala!", "hrisheekeshgnair@gmail.com")
  }
}