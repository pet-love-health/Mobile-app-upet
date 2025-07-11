package pe.edu.upc.upet.feature_notification.domain

import java.time.LocalDateTime
import java.util.Date

data class Reminder (
    val userId: Int,
    val title: String,
    val description: String,
    val dateTime: String
)