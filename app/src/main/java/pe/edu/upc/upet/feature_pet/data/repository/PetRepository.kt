package pe.edu.upc.upet.feature_pet.data.repository

import androidx.compose.runtime.Composable
import okhttp3.ResponseBody
import pe.edu.upc.upet.feature_pet.data.mapper.toDomainModel
import pe.edu.upc.upet.feature_pet.data.remote.PetRequest
import pe.edu.upc.upet.feature_pet.data.remote.PetResponse
import pe.edu.upc.upet.feature_pet.data.remote.PetService
import pe.edu.upc.upet.feature_pet.data.remote.PetServiceFactory
import pe.edu.upc.upet.feature_pet.domain.Pet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PetRepository(private val petService: PetService = PetServiceFactory.getPetService()){

    fun getPetsByOwnerId(ownerId:Int, callback: (List<Pet>) -> Unit){
        petService.getByOwnerId(ownerId).enqueue(object : Callback<List<PetResponse>> {
            override fun onResponse(call: Call<List<PetResponse>>, response: Response<List<PetResponse>>) {
                if (response.isSuccessful) {
                    val pets = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    callback(pets)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<PetResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun createPet(ownerId: Int, pet: PetRequest, callback: (Boolean) -> Unit){
        petService.createPet(ownerId, pet).enqueue(object : Callback<PetResponse> {
            override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                callback(false)
            }
        })

    }

    fun updatePet(petId: Int, pet: PetRequest, callback: (Boolean) -> Unit){
        petService.updatePet(petId, pet).enqueue(object : Callback<PetResponse> {
            override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun deletePet(petId: Int, callback: (Boolean) -> Unit){
        petService.deletePet(petId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun getPetById(petId: Int, callback: (Pet?) -> Unit) {
        petService.getPetById(petId).enqueue(object : Callback<PetResponse> {
            override fun onResponse(call: Call<PetResponse>, response: Response<PetResponse>) {
                if (response.isSuccessful) {
                    val pet = response.body()?.toDomainModel()
                    callback(pet)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    /*fun downloadMedicalReport(petId: Int, callback : @Composable (String?) -> Unit) {
        petService.downloadMedicalReport(petId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callback("https://web-production-4270c.up.railway.app/api/v1/pets/$petId/medical-report")
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(null)
            }
        })
    }*/
}