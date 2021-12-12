package ru.mypoint.notificationservice

import com.google.gson.Gson
import ru.mypoint.notificationservice.dto.EmailMessage
import ru.mypoint.notificationservice.dto.MessageFromQueue
import ru.mypoint.notificationservice.dto.TypeNotification

/** Класс контроллер для выбора способа нотификации */
class Notificator(message: String) {
    init {
        val messageFromQueue = try {
            Gson().fromJson(message, MessageFromQueue::class.java)
        } catch (error: Throwable) {
            println(error.message)
            null
        }

        when(messageFromQueue?.type) {
            TypeNotification.EMAIL -> emailNotification(messageFromQueue)

            else -> println("undefined type" + messageFromQueue?.type)
        }

    }

    /** нотификация по email */
    private fun emailNotification(messageFromQueue: MessageFromQueue) {
        SenderEmail.sendEmail(
            EmailMessage(
                messageFromQueue.subject,
                messageFromQueue.template,
                messageFromQueue.recipients,
                messageFromQueue.altMsgText
            )
        )
    }
}