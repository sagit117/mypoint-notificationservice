package ru.mypoint.notificationservice.connectors

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail

class Mailer(configMailer: ConfigMailer) {
    private val email = HtmlEmail()

    init {
        email.hostName = configMailer.hostName
        email.setSmtpPort(configMailer.smtpPort)
        email.setAuthenticator(DefaultAuthenticator(configMailer.user, configMailer.password))
        email.isSSLOnConnect = configMailer.isSSLOnConnect
        email.setFrom(configMailer.from)
        email.setCharset(configMailer.charSet)
    }

    fun send(subject: String, msgHtml: String, emails: Set<String>, altMsgText: String? = "Your email client does not support HTML messages") {
        email.subject = subject
        email.setMsg(msgHtml)

        email.setHtmlMsg(msgHtml)
        email.setTextMsg(altMsgText)

        for(mail in emails) {
            email.addTo(mail)
        }

        email.send()
    }
}

data class ConfigMailer(
    val hostName: String,
    val smtpPort: Int = 587,
    val user: String,
    val password: String,
    val from: String,
    val isSSLOnConnect: Boolean = true,
    val charSet: String = "utf-8"
)
