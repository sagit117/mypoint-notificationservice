package ru.mypoint.notificationservice

import com.google.gson.Gson
import ru.mypoint.notificationservice.dto.MessageFromQueue
import ru.mypoint.notificationservice.templates.entryMail

/** класс контроллер для выбора способа нотификации */
class Notificator(message: String) {
    init {
        val messageFromQueue = Gson().fromJson(message, MessageFromQueue::class.java)

        when(messageFromQueue.type) {
            "LOGIN" -> loginNotification(messageFromQueue)

            else -> println("undefined type" + messageFromQueue.type)
        }

    }

    /** нотификация при авторизации */
    private fun loginNotification(messageFromQueue: MessageFromQueue) {
        SenderEmail.sendEmail("Вы вошли в систему", entryMail(), setOf(messageFromQueue.userEmail), "Вы вошли в систему, если это были не Вы, восстановите пароль!")
    }
}