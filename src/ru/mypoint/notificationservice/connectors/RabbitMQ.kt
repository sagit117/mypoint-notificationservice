package ru.mypoint.notificationservice.connectors

import com.rabbitmq.client.*
import io.ktor.application.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import ru.mypoint.notificationservice.Notificator
import java.nio.charset.StandardCharsets

@Suppress("unused") // Referenced in application.conf
fun Application.rabbitModule() {
    val config = RabbitConnectionConfig(
        user = environment.config.propertyOrNull("rabbitmq.user")?.getString() ?: "guest",
        password = environment.config.propertyOrNull("rabbitmq.password")?.getString() ?: "guest",
        host = environment.config.propertyOrNull("rabbitmq.host")?.getString(),
        vHost = environment.config.propertyOrNull("rabbitmq.vHost")?.getString(),
        port = environment.config.propertyOrNull("rabbitmq.port")?.getString(),
        exNotification = environment.config.propertyOrNull("rabbitmq.exNotification")?.getString(),
        keyNotification = environment.config.propertyOrNull("rabbitmq.keyNotification")?.getString(),
        queueNotification = environment.config.propertyOrNull("rabbitmq.queueNotification")?.getString(),
    )

    if (RabbitMQ.setConnection(config, log) != null) {
        RabbitMQ.getNotification()
    } else {
        RabbitMQ.checkConnection()
    }
}

// класс для хранения настроек подключения
data class RabbitConnectionConfig(
    val user: String,
    val password: String,
    val host: String? = "127.0.0.1",
    val vHost: String? = "/",
    val port: String? = "5672",
    val exNotification: String? = "ex.notification",
    val keyNotification: String? = "k_notification",
    val queueNotification: String? = "q_notification"
)

object RabbitMQ {
    private var connection: Connection? = null
    private var configConnection: RabbitConnectionConfig? = null
    private var logger: Logger? = null
    private var notificationChannel: Channel? = null

    fun setConnection(config: RabbitConnectionConfig, log: Logger): Connection? {
        val factory = ConnectionFactory()

        factory.username = config.user
        factory.password = config.password
        factory.virtualHost = config.vHost
        factory.host = config.host
        factory.port = config.port?.toInt() ?: 5672

        connection = try {
            factory.newConnection("Notification-Service")
        } catch (error: Throwable) {
            log.error("RabbitMQ connection error: " + error.message)
            null
        }

        if (logger == null) logger = log
        if (configConnection == null) configConnection = config

        return connection
    }

    fun getNotification() {
        try {
            val channel = if (notificationChannel?.isOpen == true) notificationChannel!! else connection!!.createChannel()

            channel.exchangeDeclare(configConnection?.exNotification, BuiltinExchangeType.DIRECT, true)
            channel.queueDeclare(configConnection?.queueNotification, true, false, false, null)
            channel.queueBind(configConnection?.queueNotification, configConnection?.exNotification, configConnection?.keyNotification)

            val consumerTag = "NotificationConsumer"

            logger?.info("[$consumerTag] Waiting for messages...")

            val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
                val message = String(delivery.body, StandardCharsets.UTF_8)

                logger?.info("[$consumerTag] Received message: '$message'")

                Notificator(message)
            }
            val cancelCallback = CancelCallback { consumerTag: String? ->
                logger?.warn("[$consumerTag] was canceled")

                getNotification()
            }

            channel.basicConsume(configConnection?.queueNotification, true, consumerTag, deliverCallback, cancelCallback)
        } catch (error: Throwable) {
            logger?.error("RabbitMQ get notification error: " + error.message)
        }
    }

    fun checkConnection() {
        if (configConnection != null && logger != null) {
            runBlocking {
                launch {
                    while (setConnection(configConnection!!, logger!!) == null) {
                        delay(10000L)
                    }

                    getNotification()
                }
            }
        }
    }
}