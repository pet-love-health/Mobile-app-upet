package pe.edu.upc.upet.feature_vetClinic.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VeterinaryClinicService {
    @GET("veterinary_clinics")
    fun getAll(): Call<VeterinaryClinicResponseList>

    @POST("veterinary_clinics")
    fun createVeterinaryClinic(
        @Body veterinaryClinicRequest: VeterinaryClinicRequest
    ): Call<VeterinaryClinicResponse>

    @GET("veterinary_clinics/{clinic_id}")
    fun getById(@Path("clinic_id") clinicId: Int): Call<VeterinaryClinicResponse>

    @GET("favoriteClinics/userId/{user_id}")
    fun getFavoriteByUserId(@Path("user_id") userId: Int): Call<VeterinaryClinicResponseList>

    @GET("veterinary_clinics/generate_password/{clinic_id}")
    fun generatePassword(
        @Path("clinic_id") clinicId: Int
    ): Call<String>

    @POST("favoriteClinics/userId/{user_id}/clinicId/{clinic_id}")
    fun toggle(@Path("user_id") userId: Int, @Path("clinic_id") clinicId: Int): Call<Boolean>
}