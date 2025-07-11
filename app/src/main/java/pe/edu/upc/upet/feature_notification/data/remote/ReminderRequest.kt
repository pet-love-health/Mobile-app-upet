package pe.edu.upc.upet.feature_notification.data.remote

import java.util.Date

data class ReminderRequest(
    val title: String,
    val description: String,
    val date_time: String,
    val user_id: Int,
)

data class ReminderResponse(
    val id: Int,
    val title: String,
    val description: String,
    val date_time: String,
    val user_id: Int,
)
