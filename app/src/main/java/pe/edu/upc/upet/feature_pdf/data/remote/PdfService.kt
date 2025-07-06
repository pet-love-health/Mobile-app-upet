package pe.edu.upc.upet.feature_pdf.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PdfService {

    @GET("api/v1/pets/{petId}/medical-report")
    fun downloadMedicalReport(@Path("petId") petId: Int): Call<ResponseBody>

}

