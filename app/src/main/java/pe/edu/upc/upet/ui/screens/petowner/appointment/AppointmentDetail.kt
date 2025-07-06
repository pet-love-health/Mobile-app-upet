package pe.edu.upc.upet.ui.screens.petowner.appointment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import pe.edu.upc.upet.feature_appointment.data.remote.AppointmentUpdateRequest
import pe.edu.upc.upet.feature_appointment.data.repository.AppointmentRepository
import pe.edu.upc.upet.feature_appointment.domain.Appointment
import pe.edu.upc.upet.feature_pet.data.repository.PetRepository
import pe.edu.upc.upet.feature_pet.domain.Pet
import pe.edu.upc.upet.feature_petOwner.data.repository.PetOwnerRepository
import pe.edu.upc.upet.feature_petOwner.domain.PetOwner
import pe.edu.upc.upet.feature_vet.data.repository.VetRepository
import pe.edu.upc.upet.feature_vet.domain.Vet
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.getRole
import pe.edu.upc.upet.ui.screens.petowner.vetclinic.capitalizeFirstLetter
import pe.edu.upc.upet.ui.shared.CustomButton
import pe.edu.upc.upet.ui.shared.SuccessDialog
import pe.edu.upc.upet.ui.shared.TextSubtitle2
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.shared.getAge
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.ui.theme.Pink
import pe.edu.upc.upet.ui.theme.PinkStrong
import pe.edu.upc.upet.ui.theme.poppinsFamily
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentDetail(navController: NavController, appointmentId: Int) {
    var appointment by remember { mutableStateOf<Appointment?>(null) }
    var vet by remember { mutableStateOf<Vet?>(null) }
    var pet by remember { mutableStateOf<Pet?>(null) }
    var ownerPet by remember { mutableStateOf<PetOwner?>(null) }
    val showSuccessDialog = remember { mutableStateOf(false) }

    if (showSuccessDialog.value) {
        SuccessDialog(onDismissRequest = {
            showSuccessDialog.value = false
            navController.navigate(Routes.AppointmentList.route)
        }, titleText = "Appointment Cancelled",
            messageText = "Your appointment has been cancelled successfully.",
            buttonText = "OK")
    }
    LaunchedEffect(key1 = appointmentId) {
        AppointmentRepository().getAppointmentById(appointmentId) {
            appointment = it
        }
    }
    appointment ?: return

    LaunchedEffect(key1 = appointment?.veterinarianId) {
        VetRepository().getVetById(appointment!!.veterinarianId) {
            vet = it
        }
    }

    vet ?: return

    LaunchedEffect(key1 = appointment?.petId) {
        PetRepository().getPetById(appointment!!.petId) {
            pet = it
        }
    }

    pet ?: return

    LaunchedEffect(key1 = pet?.petOwnerId) {
        PetOwnerRepository().getPetOwnerById(pet!!.petOwnerId) {
            ownerPet = it
        }
    }

    ownerPet ?: return

    Scaffold(
        topBar = {
            TopBar(
                title = "Appointment Details", navController = navController
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(BorderPadding)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            TextSubtitle2("Schedule Appointment")

            AppointmentScheduleInfo(
                appointment!!.day,
                appointment!!.startTime, appointment!!.endTime
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextSubtitle2("Patient Information")

            PatientInformation(pet!!, appointment!!, navController)

            Spacer(modifier = Modifier.height(20.dp))

            if (getRole() == "Vet") {
                TextSubtitle2("Owner Information")

                OwnerInformation(ownerPet!!, navController)

                Spacer(modifier = Modifier.height(20.dp))

                CustomButton(text = "Add report") {
                    navController.navigate(Routes.AddReport.createRoute(pet!!.id))
                }
            } else {
                TextSubtitle2("Veterinary Information")

                VetInformation(vet!!, navController)
            }
            CustomButton(text = "Complete") {
                navController.navigate(Routes.CompleteAppointment.createRoute(appointmentId))
            }
            CustomButton(text = "Cancel") {
                val appointment = AppointmentUpdateRequest(
                    diagnosis = "Cancelled",
                    treatment = "Cancelled"
                )

                AppointmentRepository().cancelAppointment(appointmentId,appointment){
                    if (it) showSuccessDialog.value = true
                    else Log.d("AppointmentCancelScreen", "Error cancelling appointment")
                }
            }
        }
    }
}

@Composable
fun InformationCard(
    imageUrl: String,
    name: String,
    subtitle: String,
    id: Int,
    navController: NavController
) {
    Card(
        modifier = Modifier

            .fillMaxWidth()
            .clickable(onClick = {
                navController.navigate(Routes.OwnerVetProfile.createRoute(id))
            }),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffFFFFFf),
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x37FFb0bb), RoundedCornerShape(4.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlideImage(
                imageModel = { imageUrl },
                modifier = Modifier
                    .size(60.dp)
                    .border(
                        width = 2.dp,
                        color = Pink,
                        shape = RoundedCornerShape(100)
                    )
                    .padding(5.dp)
                    .clip(RoundedCornerShape(100)),
                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
            )
            Column {
                Text(
                    text = capitalizeFirstLetter(name),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsFamily
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        color = PinkStrong,
                        fontSize = 16.sp,
                    )
                )
            }
        }
    }
}

@Composable
fun OwnerInformation(ownerPet: PetOwner, navController: NavController) {
    InformationCard(
        ownerPet.imageUrl,
        ownerPet.name,
        ownerPet.numberPhone,
        ownerPet.id,
        navController
    )
}

@Composable
fun VetInformation(vet: Vet, navController: NavController) {
    InformationCard(vet.imageUrl,
        "Dr. ${vet.name}",
        "Specialty: Veterinary", vet.id, navController)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentScheduleInfo(day: String, startHour: String, endHour: String) {
    val formattedDay = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        .format(DateTimeFormatter.ofPattern("d MMMM", java.util.Locale("es", "ES")))
    val formattedEndTime = LocalTime.parse(endHour, DateTimeFormatter.ofPattern("HH:mm:ss"))
        .format(DateTimeFormatter.ofPattern("h:mm a"))
    val formattedStartTime = LocalTime.parse(startHour, DateTimeFormatter.ofPattern("HH:mm:ss"))
        .format(DateTimeFormatter.ofPattern("h:mm a"))
    val time = "$formattedStartTime - $formattedEndTime"

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Date",
                tint = Blue1
            )
            Text(text = formattedDay, color = Color(0xFFFF6262))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Time",
                tint = Blue1
            )
            Text(text = time, color = Color(0xFFFF6262))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PatientInformation(pet: Pet, appointment: Appointment, navController: NavController) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Problem: ",
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontFamily = poppinsFamily
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = appointment.description,
            style = TextStyle(
                color = PinkStrong,
                fontSize = 16.sp,
            )
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    Card(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = {
                navController.navigate(Routes.PetDetails.createRoute(pet.id))
            }),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffFFFFFf),
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x37FFb0bb), RoundedCornerShape(4.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            GlideImage(
                imageModel = { pet.image_url },
                modifier = Modifier
                    .size(60.dp)
                    .border(
                        width = 2.dp,
                        color = Pink,
                        shape = RoundedCornerShape(100)
                    )
                    .padding(5.dp)
                    .clip(RoundedCornerShape(100)),
                imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = " ${capitalizeFirstLetter(pet.name)}",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = poppinsFamily
                        )
                    )
                    Text(
                        text = "-     ${getAge(pet.birthdate)}",
                        style = TextStyle(
                            color = PinkStrong,
                            fontSize = 16.sp,
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = " Breed: ${capitalizeFirstLetter(pet.breed)}",
                    style = TextStyle(
                        color = PinkStrong,
                        fontSize = 16.sp,
                    )
                )
            }
        }
    }
}