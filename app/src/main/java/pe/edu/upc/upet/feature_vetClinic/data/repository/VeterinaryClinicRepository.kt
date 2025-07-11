package pe.edu.upc.upet.feature_vetClinic.data.repository

import android.util.Log
import pe.edu.upc.upet.feature_vetClinic.data.mapper.toDomainModel
import pe.edu.upc.upet.feature_vetClinic.data.remote.VeterinaryClinicRequest
import pe.edu.upc.upet.feature_vetClinic.data.remote.VeterinaryClinicResponse
import pe.edu.upc.upet.feature_vetClinic.data.remote.VeterinaryClinicResponseList
import pe.edu.upc.upet.feature_vetClinic.data.remote.VeterinaryClinicService
import pe.edu.upc.upet.feature_vetClinic.data.remote.VeterinaryClinicServiceFactory
import pe.edu.upc.upet.feature_vetClinic.domain.Veterinaries
import pe.edu.upc.upet.feature_vetClinic.domain.VeterinaryClinic
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VeterinaryClinicRepository {
    private val veterinaryClinicService: VeterinaryClinicService = VeterinaryClinicServiceFactory.getVeterinaryService()

    fun getAllVeterinaryClinics(callback: (Veterinaries)-> Unit){
        veterinaryClinicService.getAll().enqueue(object : Callback<VeterinaryClinicResponseList> {
            override fun onResponse(
                call: Call<VeterinaryClinicResponseList>,
                response: Response<VeterinaryClinicResponseList>
            ) {
                if (response.isSuccessful){
                    val veterinaryClinics = response.body()?.map { it.toDomainModel() }?: emptyList()
                    callback(veterinaryClinics)
                }else{
                    Log.e("VeterinaryClinicRepository", "Failed to get veterinary clinics: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<VeterinaryClinicResponseList>, t: Throwable) {
                Log.e("VeterinaryClinicRepository", "Failed to get veterinary clinics", t)
            }
        })
    }

    fun getVeterinaryClinicById(clinicId: Int, callback: (VeterinaryClinic?) -> Unit) {
        veterinaryClinicService.getById(clinicId).enqueue(object :
            Callback<VeterinaryClinicResponse> {
            override fun onResponse(call: Call<VeterinaryClinicResponse>, response: Response<VeterinaryClinicResponse>) {
                if (response.isSuccessful) {
                    val veterinaryClinic = response.body()?.toDomainModel()
                    callback(veterinaryClinic)
                } else {
                    Log.e("VeterinaryClinicRepository", "Failed to get veterinary clinic by id: ${response.errorBody()}")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<VeterinaryClinicResponse>, t: Throwable) {
                Log.e("VeterinaryClinicRepository", "Failed to get veterinary clinic by id", t)
                callback(null)
            }
        })
    }

    fun createVeterinaryClinic(veterinaryClinicData: VeterinaryClinicRequest, callback: (VeterinaryClinic?) -> Unit){
        veterinaryClinicService.createVeterinaryClinic(veterinaryClinicData).enqueue(object :
            Callback<VeterinaryClinicResponse> {
            override fun onResponse(
                call: Call<VeterinaryClinicResponse>,
                response: Response<VeterinaryClinicResponse>
            ) {
                if (response.isSuccessful){
                    Log.d("CreateVeterinaryClinic", "data sent: ${veterinaryClinicData.name}")
                    Log.d("CreateVeterinaryClinic", "Response: ${response.body()?.toString()}")

                    val veterinaryClinic = response.body()?.toDomainModel()
                    Log.d("CreateVeterinaryClinic", "Response body: ${response.body()?.toString()}")
                    Log.d("CreateVeterinaryClinic", "Converted veterinary clinic: ${veterinaryClinic?.toString()}")
                    callback(veterinaryClinic)
                }else{
                    Log.e("CreateVeterinaryClinic", "Unsuccessful response: ${response.code()}")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<VeterinaryClinicResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun generatePassword(clinicId: Int, callback: (String?) -> Unit){
        veterinaryClinicService.generatePassword(clinicId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    val password = response.body()
                    callback(password)
                }else{
                    callback(null)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                callback(null)
            }
        })
    }
    fun getFavoriteVeterinaryClinics(userId: Int, callback: (Veterinaries)-> Unit){
        veterinaryClinicService.getFavoriteByUserId(userId).enqueue(object : Callback<VeterinaryClinicResponseList> {
            override fun onResponse(
                call: Call<VeterinaryClinicResponseList>,
                response: Response<VeterinaryClinicResponseList>
            ) {
                if (response.isSuccessful){
                    val veterinaryClinics = response.body()?.map { it.toDomainModel() }?: emptyList()
                    callback(veterinaryClinics)
                }else{
                    Log.e("VeterinaryClinicRepository", "Failed to get veterinary clinics: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<VeterinaryClinicResponseList>, t: Throwable) {
                Log.e("VeterinaryClinicRepository", "Failed to get veterinary clinics", t)
            }
        })
    }
    fun toggle(userId: Int, clinicId: Int, callback: (Boolean) -> Unit) {
        veterinaryClinicService.toggle(userId, clinicId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    Log.e("VeterinaryClinicRepository", "Failed to add favorite veterinary clinic: ${response.errorBody()}")
                    callback(false)
                }
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.e("VeterinaryClinicRepository", "Failed to add favorite veterinary clinic", t)
                callback(false)
            }
        })
    }
}
