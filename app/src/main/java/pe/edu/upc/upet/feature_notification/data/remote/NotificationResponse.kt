package pe.edu.upc.upet.feature_notification.data.remote

data class NotificationResponse(
    val type: String,
    val message: String,
    val datetime: String,
    val targetId: Int,
    val title: String,
    val id: Int,
    val isRead: Boolean,
    val targetType: String,
)
