package pe.edu.upc.upet.feature_notification.data.mapper

import pe.edu.upc.upet.feature_notification.data.remote.NotificationResponse
import pe.edu.upc.upet.feature_notification.data.remote.ReminderRequest
import pe.edu.upc.upet.feature_notification.domain.Notification
import pe.edu.upc.upet.feature_notification.domain.Reminder

fun NotificationResponse.toDomain(): Notification {
    return Notification(
        id = this.id,
        title = this.title,
        type = this.type,
        message = this.message,
        datetime = this.datetime
    )
}

fun ReminderRequest.toDomain(): Reminder {
    return Reminder(
        title = this.title,
        description = this.description,
        userId = this.user_id,
        dateTime = this.date_time
    )
}