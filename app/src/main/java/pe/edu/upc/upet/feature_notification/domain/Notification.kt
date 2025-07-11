package pe.edu.upc.upet.feature_notification.domain

import java.time.LocalDateTime

data class Notification (
    val id: Int,
    val title: String,
    val type: String,
    val message: String,
    val datetime: String
)