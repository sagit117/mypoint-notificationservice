package ru.mypoint.notificationservice

import io.ktor.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mypoint.notificationservice.connectors.ConfigMailer
import ru.mypoint.notificationservice.connectors.Mailer
import ru.mypoint.notificationservice.dto.EmailMessage

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
        SenderEmail.config = config
    }
}

object SenderEmail {
    var mailer: Mailer? = null
    var config: ConfigMailer? = null
    private val messageQueue: MutableList<EmailMessage> = emptyList<EmailMessage>().toMutableList()

    init {
        sendQueue()
    }

    fun sendEmail(email: EmailMessage) {
        messageQueue.add(email)
//        mailer?.send(email.subject, email.msgHtml, email.emails, email.altMsgText)
    }

    private fun sendQueue() {
        GlobalScope.launch {
            delay(1000L)

            if (messageQueue.isNotEmpty()) {
                try {
                    mailer?.send(
                        messageQueue.component1().subject,
                        messageQueue.component1().msgHtml,
                        messageQueue.component1().emails,
                        messageQueue.component1().altMsgText
                    )

                    messageQueue.removeAt(0)
                } catch (error: Throwable) {
                    println("Send Email Error: ${error.message}")

                    mailer = config?.let { Mailer(it) }
                }
            }

            sendQueue()
        }
    }
}