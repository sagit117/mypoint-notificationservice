package ru.mypoint.notificationservice.dto

data class MessageFromQueue(
    val type: TypeNotification,
    val recipients: Set<String>,
    val template: String,
    val subject: String = "",
    val altMsgText: String = "",
)

enum class TypeNotification {
    EMAIL
}
