package ru.mypoint.notificationservice

import com.google.gson.Gson
import ru.mypoint.notificationservice.dto.MessageFromQueue
import ru.mypoint.notificationservice.dto.TypeNotification

/** класс контроллер для выбора способа нотификации */
class Notificator(message: String) {
    init {
        val messageFromQueue = Gson().fromJson(message, MessageFromQueue::class.java)

        when(messageFromQueue.type) {
            TypeNotification.EMAIL -> emailNotification(messageFromQueue)

            else -> println("undefined type" + messageFromQueue.type)
        }

    }

    /** нотификация по email */
    private fun emailNotification(messageFromQueue: MessageFromQueue) {
        SenderEmail.sendEmail(
            messageFromQueue.subject,
            messageFromQueue.template,
            messageFromQueue.recipients,
            messageFromQueue.altMsgText
        )
    }
}