package pe.edu.upc.upet.feature_notification.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {
    @GET("notifications/pet-owner/{pet_owner_id}")
    fun getByOwnerId(@Path("pet_owner_id") pet_owner_Id: Int): Call<List<NotificationResponse>>

    @GET("notifications/veterinarian/{veterinarian_id}")
    fun getByVetId(@Path("veterinarian_id") veterinarian_id: Int): Call<List<NotificationResponse>>

    @POST("reminders")
    fun createReminder(@Body reminderRequest: ReminderRequest): Call<ReminderResponse>
}
