package pe.edu.upc.upet.feature_appointment.data.repository

import android.util.Log
import pe.edu.upc.upet.feature_appointment.data.mapper.toDomainModel
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentRequest
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentResponse
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentService
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentServiceFactory
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentUpdateRequest
import pe.edu.upc.upet.feature_appointment.domain.Appointment
import pe.edu.upc.upet.feature_vet.data.remote.VetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentRepository(
    private val appointmentService: AppointmentService = AppointmentServiceFactory.getAppointmentService()
) {
    fun createAppointment(appointment: AppointmentRequest, callback: (Boolean) -> Unit){
        appointmentService.createAppointment(appointment).enqueue(object :
            Callback<AppointmentResponse> {
            override fun onResponse(call: Call<AppointmentResponse>, response: Response<AppointmentResponse>) {
                if (response.isSuccessful) {
                    val appointmentResponse = response.body()?.toDomainModel()
                    callback(true)
                } else {
                    callback(false)
                }
            }
            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun getAppointments(callback: (List<Appointment>) -> Unit) {
        appointmentService.getAppointments().enqueue(object : Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getAppointmentById(id: Int, callback: (Appointment?) -> Unit) {
        appointmentService.getAppointmentById(id).enqueue(object : Callback<AppointmentResponse> {
            override fun onResponse(call: Call<AppointmentResponse>, response: Response<AppointmentResponse>) {
                if (response.isSuccessful) {
                    val appointment = response.body()?.toDomainModel()
                    callback(appointment)
                } else {
                    callback(null)
                }
            }
            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getAppointmentsByPetId(petId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getAppointmentsByPetId(petId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getAppointmentsByVeterinarianId(veterinarianId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getAppointmentsByVeterinarianId(veterinarianId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getAppointmentsByOwnerId(ownerId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getAppointmentsByOwnerId(ownerId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun updateAppointment(appointmentId: Int, appointment: AppointmentUpdateRequest, callback: (Boolean) -> Unit) {
        appointmentService.updateAppointment(appointmentId, appointment).enqueue(object :
            Callback<AppointmentResponse> {
            override fun onResponse(call: Call<AppointmentResponse>, response: Response<AppointmentResponse>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun getUpcomingAppointmentsByOwnerId(ownerId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getUpcomingAppointmentsByOwnerId(ownerId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getPastAppointmentsByOwnerId(ownerId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getPastAppointmentsByOwnerId(ownerId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getCancelledAppointmentsByOwnerId(ownerId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getCancelledAppointmentsByOwnerId(ownerId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getUpcomingAppointmentsByVeterinarianId(veterinarianId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getUpcomingAppointmentsByVeterinarianId(veterinarianId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                    Log.d("UpcomingAppointments", appointments.toString())
                } else {
                    callback(emptyList())
                    Log.d("UpcomingAppointments", "Empty")
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getPastAppointmentsByVeterinarianId(veterinarianId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getPastAppointmentsByVeterinarianId(veterinarianId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getCancelledAppointmentsByVeterinarianId(veterinarianId: Int, callback: (List<Appointment>) -> Unit) {
        appointmentService.getCancelledAppointmentsByVeterinarianId(veterinarianId).enqueue(object :
            Callback<List<AppointmentResponse>> {
            override fun onResponse(call: Call<List<AppointmentResponse>>, response: Response<List<AppointmentResponse>>) {
                if (response.isSuccessful) {
                    val appointments = response.body()?.map { it.toDomainModel() }
                    callback(appointments ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }
            override fun onFailure(call: Call<List<AppointmentResponse>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun cancelAppointment(appointmentId: Int, appointment: AppointmentUpdateRequest, callback: (Boolean) -> Unit) {
        appointmentService.cancelAppointment(appointmentId, appointment).enqueue(object :
            Callback<AppointmentResponse> {
            override fun onResponse(call: Call<AppointmentResponse>, response: Response<AppointmentResponse>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
            override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                callback(false)
            }
        })
    }
}
