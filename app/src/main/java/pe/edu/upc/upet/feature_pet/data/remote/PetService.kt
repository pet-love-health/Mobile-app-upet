package pe.edu.upc.upet.feature_pet.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PetService {
    @GET("pets/{petowner_id}")
    fun getByOwnerId(@Path("petowner_id") petowner_Id: Int): Call<List<PetResponse>>

    @POST("pets/{petowner_id}")
    fun createPet(@Path("petowner_id") petowner_Id: Int, @Body petRequest: PetRequest): Call<PetResponse>

    @PUT("pets/{pet_id}")
    fun updatePet(@Path("pet_id") pet_Id: Int, @Body petRequest: PetRequest): Call<PetResponse>

    @GET("pets/pet/{pet_id}")
    fun getPetById(@Path("pet_id") petId: Int): Call<PetResponse>

    @DELETE("pets/{pet_id}")
    fun deletePet(@Path("pet_id") pet_Id: Int): Call<Unit>

    @GET("api/v1/pets/{petId}/medical-report")
    fun downloadMedicalReport(@Path("petId") petId: Int): Call<ResponseBody>
}