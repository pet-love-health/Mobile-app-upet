package pe.edu.upc.upet.ui.screens.vet

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pe.edu.upc.upet.feature_appointment.data.repository.AppointmentRepository
import pe.edu.upc.upet.feature_appointment.domain.Appointment
import pe.edu.upc.upet.feature_pet.data.repository.PetRepository
import pe.edu.upc.upet.feature_pet.domain.Pet
import pe.edu.upc.upet.feature_vet.data.repository.VetRepository
import pe.edu.upc.upet.feature_vet.domain.Vet
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.appointment.AppointmentCardDetails
import pe.edu.upc.upet.ui.screens.petowner.appointment.AppointmentFilterButtons
import pe.edu.upc.upet.ui.screens.petowner.appointment.ImageCircle
import pe.edu.upc.upet.ui.screens.petowner.getVet
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.theme.BorderPadding

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VetAppointments(navController: NavController) {
    var upcomingAppointments by remember { mutableStateOf(listOf<Appointment>()) }
    var pastAppointments by remember { mutableStateOf(listOf<Appointment>()) }
    var cancelledAppointments by remember { mutableStateOf(listOf<Appointment>()) }
    var showUpcoming by remember { mutableStateOf(1) } // This is already set to true

    val vet = getVet() ?: return

    LaunchedEffect(vet.id) {
        AppointmentRepository().getUpcomingAppointmentsByVeterinarianId(vet.id) { upcomingVetAppointments ->
            Log.d("UpcomingVetAppointments", upcomingVetAppointments.toString())
            upcomingAppointments = upcomingVetAppointments
        }
        AppointmentRepository().getPastAppointmentsByVeterinarianId(vet.id) { pastVetAppointments ->
            Log.d("PastVetAppointments", pastVetAppointments.toString())

            pastAppointments = pastVetAppointments
        }
        AppointmentRepository().getCancelledAppointmentsByVeterinarianId(vet.id) { cancelledVetAppointments ->
            Log.d("CancelledVetAppointments", cancelledVetAppointments.toString())

            cancelledAppointments = cancelledVetAppointments
        }
    }
    val appointments : List<Appointment>;
    if (showUpcoming == 1) {appointments = upcomingAppointments}
    else if (showUpcoming == 2) {appointments = pastAppointments}
    else { appointments = cancelledAppointments }

    Scaffold(
        topBar = { TopBar(navController = navController, title = "My Appointments") },
        modifier = Modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AppointmentFilterButtons(
                onShowUpcomingChange = { upcoming ->
                    showUpcoming = upcoming
                }
            )
            AppointmentListContentVet(appointments, navController)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentListContentVet(
    appointments: List<Appointment>,
    navController: NavController
) {
    LazyColumn {
        items(appointments.size) { index ->
            AppointmentCardVet(appointments[index], navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCardVet(appointment: Appointment, navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = BorderPadding, end = BorderPadding)
            .clickable { navController.navigate(Routes.AppointmentDetail.createRoute(appointment.id)) },
    ) {
        AppointmentCardInfoVet(appointment)
        //DividerAndButtons()
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCardInfoVet(appointment: Appointment) {
    var vet by remember { mutableStateOf<Vet?>(null) }
    var pet by remember { mutableStateOf<Pet?>(null) }

    LaunchedEffect(appointment.veterinarianId) {
        VetRepository().getVetById(appointment.veterinarianId) {
            vet = it
        }
    }

    LaunchedEffect(appointment.petId) {
        PetRepository().getPetById(appointment.petId) {
            pet = it
        }
    }

    vet?: return
    pet?: return

    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F6FF),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(6.dp)
        ) {
            ImageCircle(imageUrl = pet!!.image_url)
            AppointmentCardDetails(
                pet!!.name,
                vet!!.name,
                appointment.status,
                appointment.startTime,
                appointment.endTime,
                appointment.day
            )
        }
    }
}