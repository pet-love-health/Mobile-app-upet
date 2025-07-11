package pe.edu.upc.upet.feature_notification.data.repository

import pe.edu.upc.upet.feature_notification.data.mapper.toDomain
import pe.edu.upc.upet.feature_notification.data.remote.NotificationResponse
import pe.edu.upc.upet.feature_notification.data.remote.NotificationService
import pe.edu.upc.upet.feature_notification.data.remote.NotificationServiceFactory
import pe.edu.upc.upet.feature_notification.data.remote.ReminderRequest
import pe.edu.upc.upet.feature_notification.data.remote.ReminderResponse
import pe.edu.upc.upet.feature_notification.domain.Notification
import pe.edu.upc.upet.feature_notification.domain.Reminder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRepository(
    private val notificationService: NotificationService = NotificationServiceFactory.getNotificationService()
){
    fun getByOwnerId(ownerId:Int, callback: (List<Notification>) -> Unit){
        notificationService.getByOwnerId(ownerId).enqueue(object : Callback<List<NotificationResponse>> {
            override fun onResponse(call: Call<List<NotificationResponse>>, response: Response<List<NotificationResponse>>) {
                if (response.isSuccessful) {
                    val notifs = response.body()?.map { it.toDomain() } ?: emptyList()
                    callback(notifs)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
    fun getByVetId(vetId:Int, callback: (List<Notification>) -> Unit){
        notificationService.getByVetId(vetId).enqueue(object : Callback<List<NotificationResponse>> {
            override fun onResponse(call: Call<List<NotificationResponse>>, response: Response<List<NotificationResponse>>) {
                if (response.isSuccessful) {
                    val notifs = response.body()?.map { it.toDomain() } ?: emptyList()
                    callback(notifs)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
    fun createReminder(reminder: ReminderRequest, callback: (Boolean) -> Unit) {
        notificationService.createReminder(reminder).enqueue(object : Callback<ReminderResponse> {
            override fun onResponse(call: Call<ReminderResponse>, response: Response<ReminderResponse>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ReminderResponse>, t: Throwable) {
                callback(false)
            }
        })
    }
}