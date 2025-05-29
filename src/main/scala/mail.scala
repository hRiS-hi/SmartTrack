package attendance

import java.util.Properties
import jakarta.mail._
import jakarta.mail.internet._

object EmailSender {
  private val username = "anonymous@email.com"
  private val password = "anony" // Replace with your Gmail App Password

  def sendEmail(subject: String, body: String, recipient: String): Unit = {
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.port", "587")

    val session = Session.getInstance(props, new Authenticator {
      override def getPasswordAuthentication: PasswordAuthentication = {
        new PasswordAuthentication(username, password)
      }
    })

    try {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(username))
      message.setRecipients(Message.RecipientType.TO, recipient)
      message.setSubject(subject)
      message.setText(body)

      Transport.send(message)
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"Failed to send email: ${e.getMessage}", e)
    }
  }
}