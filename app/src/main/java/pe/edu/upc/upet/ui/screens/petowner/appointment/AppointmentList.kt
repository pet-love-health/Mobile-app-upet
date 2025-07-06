    package pe.edu.upc.upet.ui.screens.petowner.appointment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.skydoves.landscapist.glide.GlideImage
import pe.edu.upc.upet.feature_appointment.data.repository.AppointmentRepository
import pe.edu.upc.upet.feature_appointment.domain.Appointment
import pe.edu.upc.upet.feature_pet.data.repository.PetRepository
import pe.edu.upc.upet.feature_pet.domain.Pet
import pe.edu.upc.upet.feature_vet.data.repository.VetRepository
import pe.edu.upc.upet.feature_vet.domain.Vet
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.getOwner
import pe.edu.upc.upet.ui.screens.petowner.isOwnerAuthenticated
import pe.edu.upc.upet.ui.screens.petowner.vetclinic.capitalizeFirstLetter
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.ui.theme.Pink
import pe.edu.upc.upet.ui.theme.poppinsFamily
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentList(navController: NavController) {
    var upcomingAppointments by remember { mutableStateOf(listOf<Appointment>()) }
    var pastAppointments by remember { mutableStateOf(listOf<Appointment>()) }
    var cancelledAppointments by remember { mutableStateOf(listOf<Appointment>()) }
    var showUpcoming by remember { mutableStateOf(1) }

    val owner = getOwner() ?: return

    LaunchedEffect(owner.id) {
        AppointmentRepository().getUpcomingAppointmentsByOwnerId(owner.id) { appointments ->
            upcomingAppointments = appointments
        }
    }

    LaunchedEffect(owner.id) {
        AppointmentRepository().getPastAppointmentsByOwnerId(owner.id) { appointments ->
            pastAppointments = appointments
        }
    }

    LaunchedEffect(owner.id) {
        AppointmentRepository().getCancelledAppointmentsByOwnerId(owner.id) { appointments ->
            cancelledAppointments = appointments
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
            AppointmentListContent(appointments, navController)
        }
    }
}



@Composable
fun AppointmentFilterButtons(
    onShowUpcomingChange: (Int) -> Unit
) {
    var isUpcomingSelected by remember { mutableStateOf(1) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(BorderPadding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                onShowUpcomingChange(1)
                isUpcomingSelected = 1
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isUpcomingSelected == 1) Pink else Color.White,
                contentColor = if (isUpcomingSelected == 1) Color.White else Pink,
            )
        ) {
            Text("Upcoming")
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                onShowUpcomingChange(2)
                isUpcomingSelected = 2
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isUpcomingSelected == 2) Pink else Color.White,
                contentColor = if (isUpcomingSelected == 2) Color.White else Pink, )
        ) {
            Text("Past")
        }
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                onShowUpcomingChange(3)
                isUpcomingSelected = 3
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isUpcomingSelected == 3) Pink else Color.White,
                contentColor = if (isUpcomingSelected == 3) Color.White else Pink,)
        ) {
            Text("Cancelled")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentListContent(
    appointments: List<Appointment>,
    navController: NavController
) {
    LazyColumn {
        items(appointments.size) { index ->
            AppointmentCard(appointments[index], navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(appointment: Appointment, navController: NavController) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = BorderPadding, end = BorderPadding)
            .clickable { navController.navigate(Routes.AppointmentDetail.createRoute(appointment.id)) },
    ) {
        AppointmentCardInfo(appointment)
        //DividerAndButtons()
    }
    Spacer(modifier = Modifier.height(15.dp))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCardInfo(appointment: Appointment) {
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

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val appointmentDate = LocalDate.parse(appointment.day, formatter)
    val today = LocalDate.now()

    vet?: return
    pet?: return

    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F6FF),
        ),
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            ImageCircle(imageUrl = vet!!.imageUrl)
            AppointmentCardDetails(vet!!.name, pet!!.name, appointment.status, appointment.startTime, appointment.endTime, appointment.day)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCardDetails(name: String, petName: String, statusText: String, startTime: String, endTime: String, day: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = capitalizeFirstLetter(name),
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontFamily = poppinsFamily,
                fontWeight = FontWeight.SemiBold
            )
        )

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .background(Color(0x37FFb0bb), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isOwnerAuthenticated()) {
                        "Vet: ${capitalizeFirstLetter(petName)}"
                    } else {
                        "Pet: ${
                            capitalizeFirstLetter(petName)
                        }"
                    },
                    color = Pink,
                    style = TextStyle(fontSize = 16.sp)
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0x37FFD700), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    color = Color(0xFFE59500),
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val formattedDay = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .format(DateTimeFormatter.ofPattern("d MMMM", Locale("es", "ES")))
            val formattedTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"))
                .format(DateTimeFormatter.ofPattern("h:mm a"))
            Text(text = "$formattedDay | $formattedTime", color = Color.Gray)
        }
    }
}


@Composable
fun ImageCircle(imageUrl: String) {
    GlideImage(
        imageModel = { imageUrl },
        modifier = Modifier
            .padding(10.dp)
            .size(100.dp)
            .clip(RoundedCornerShape(50))
    )
}