package ru.mypoint.notificationservice

import com.google.gson.Gson
import ru.mypoint.notificationservice.dto.MessageFromQueue

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
        println(messageFromQueue.toString())
    }
}