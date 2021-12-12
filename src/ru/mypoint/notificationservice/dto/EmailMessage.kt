package ru.mypoint.notificationservice.dto

data class EmailMessage(
    val subject: String,
    val msgHtml: String,
    val emails: Set<String>,
    val altMsgText: String? = null
)
