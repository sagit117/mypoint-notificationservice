package ru.mypoint.notificationservice

import io.ktor.application.*
import ru.mypoint.notificationservice.connectors.ConfigMailer
import ru.mypoint.notificationservice.connectors.Mailer

@Suppress("unused") // Referenced in application.conf
fun Application.sendEmailModule() {
    val config = ConfigMailer(
        /** настройки mailer */
        hostName = environment.config.property("mailer.hostName").getString(),
        smtpPort = environment.config.property("mailer.smtpPort").getString().toInt(),
        user = environment.config.property("mailer.user").getString(),
        password = environment.config.property("mailer.password").getString(),
        isSSLOnConnect = environment.config.property("mailer.isSSLOnConnect").getString().toBoolean(),
        from = environment.config.property("mailer.from").getString(),
        charSet = environment.config.property("mailer.charSet").getString()
    )

    if (SenderEmail.mailer == null) {
        SenderEmail.mailer = Mailer(config)
    }
}

object SenderEmail {
    var mailer: Mailer? = null

    fun sendEmail(subject: String, msgHtml: String, emails: Set<String>, altMsgText: String? = null) {
        mailer?.send(subject, msgHtml, emails, altMsgText)
    }
}