package pe.edu.upc.upet.ui.screens.petowner.appointment

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentRequest
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentUpdateRequest
import pe.edu.upc.upet.feature_appointment.data.repository.AppointmentRepository
import pe.edu.upc.upet.feature_pet.data.repository.PetRepository
import pe.edu.upc.upet.feature_pet.domain.Pet
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.shared.CustomButton
import pe.edu.upc.upet.ui.shared.DropdownMenuBox
import pe.edu.upc.upet.ui.shared.ExpandableTextField
import pe.edu.upc.upet.ui.shared.SuccessDialog
import pe.edu.upc.upet.ui.shared.TextSubtitle2
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.utils.TokenManager

@Composable
fun CompleteAppointment(navController: NavController, appointmentId: Int) {
    val petRepository = remember { PetRepository() }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val ownerId = TokenManager.getUserIdAndRoleFromToken()?.first
    val selectedPet = remember { mutableStateOf("") }
    val textDiagnosis = remember { mutableStateOf("") }
    val textTreatment = remember { mutableStateOf("") }

    if (showSuccessDialog.value) {
        SuccessDialog(onDismissRequest = {
            showSuccessDialog.value = false
            navController.navigate(Routes.AppointmentList.route)
        }, titleText = "Appointment Completed",
            messageText = "Your appointment has been completed successfully.",
            buttonText = "OK")
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Complete Appointment", navController = navController
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(top = 10.dp, start = BorderPadding, end = BorderPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Top
            ) {

                TextSubtitle2(text = "Diagnosis")
                ExpandableTextField(input = textDiagnosis, placeholder = "Enter the pet's diagnosis")
                TextSubtitle2(text = "Treatment")
                ExpandableTextField(input = textTreatment, placeholder = "Enter the pet's treatment")
            }

            CustomButton(text = "Complete", onClick = {

                updateAppointment(
                    appointmentId,
                    textDiagnosis.value,
                    textTreatment.value,
                    showSuccessDialog
                )
            })

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun updateAppointment(appointmentId: Int, textDiagnosis: String, textTreatment: String, showSuccessDialog: MutableState<Boolean>) {
    val appointment = AppointmentUpdateRequest(
        diagnosis = textDiagnosis,
        treatment = textTreatment
    )
    AppointmentRepository().updateAppointment(appointmentId,appointment){
        if (it) showSuccessDialog.value = true
        else Log.d("AppointmentUpdateScreen", "Error updating appointment")
    }
}