package pe.edu.upc.upet.ui.screens.petowner.vetclinic

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import pe.edu.upc.upet.feature_vet.data.repository.VetRepository
import pe.edu.upc.upet.feature_vet.domain.Vet
import pe.edu.upc.upet.feature_vetClinic.data.repository.VeterinaryClinicRepository
import pe.edu.upc.upet.feature_vetClinic.domain.VeterinaryClinic
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.pet.ImageRectangle
import pe.edu.upc.upet.ui.shared.Dialog
import pe.edu.upc.upet.ui.shared.TextNormal
import pe.edu.upc.upet.ui.shared.TextSemiBold
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.utils.TokenManager
import java.util.Locale

@Composable
fun OwnerClinicDetails(navController: NavHostController, vetClinicId: Int) {
    val vetClinicRepository = remember { VeterinaryClinicRepository() }
    val vetRepository = remember { VetRepository() }
    var vetClinic by remember { mutableStateOf<VeterinaryClinic?>(null) }
    var vets by remember { mutableStateOf<List<Vet>?>(null) }
    val context = LocalContext.current
    var streetName by remember { mutableStateOf("Loading...") }
    val (userId,_,_) = TokenManager.getUserIdAndRoleFromToken() ?: error("Error obteniendo el userId desde el token")
    LaunchedEffect(key1 = vetClinicId) {
        vetClinicRepository.getVeterinaryClinicById(vetClinicId) { clinic ->
            vetClinic = clinic
        }
    }

    LaunchedEffect(key1 = vetClinicId) {
        vetRepository.getVetsByClinicId(vetClinicId) { vetList ->
            vets = vetList
        }
    }

    LaunchedEffect(vetClinic?.location) {
        vetClinic?.location?.let { location ->
            streetName = getStreetNameFromCoordinates(location, context)
        }
    }

    vetClinic?.let { veterinaryClinic ->
        Scaffold(
            topBar = {
                TopBar(navController = navController, title = "Veterinary Clinic")
            },
            modifier = Modifier,
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    ImageRectangle(imageUrl = veterinaryClinic.image_url)

                    ClinicNameAndRating(name = veterinaryClinic.name,
                        ownerId = userId, clinicId = veterinaryClinic.id, vetClinicRepository)

                    ClinicInfo(
                        phoneNumber = "987654321",
                        email = veterinaryClinic.name.lowercase(Locale.ROOT) + "@" + "gmail.com",
                        operatingHours = " ${formatTime(veterinaryClinic.office_hours_start) + " - " + formatTime(veterinaryClinic.office_hours_end)}",
                        location = streetName
                    )

                    AboutUsSection(description = capitalizeFirstLetter2(veterinaryClinic.description))

                    TextSemiBold(text = "Our Specialists")

                    LazyRow {
                        items(vets ?: emptyList()) { vet ->
                            VeterinaryCard(vet, navController)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ClinicNameAndRating(name: String, ownerId: Int, clinicId: Int, veterinaryClinicRepository: VeterinaryClinicRepository) {
    val snackbarMessage = remember { mutableStateOf("") }
    val showErrorSnackbar = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = capitalizeFirstLetter(name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Blue1
        )
        Row {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFB800)
            )
            Text(text = "4.5")
        }
        IconButton(
            onClick = {
                veterinaryClinicRepository.toggle(userId = ownerId, clinicId = clinicId) { success ->
                    if (success) {

                        snackbarMessage.value = "You must enter your email."

                        showErrorSnackbar.value = true
                    } else {
                        // Handle failure, e.g., show an error message
                    }
                }
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Call",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    Dialog(message = (snackbarMessage.value), showError = showErrorSnackbar )
}

@Composable
fun ClinicInfo(phoneNumber: String, email: String, operatingHours: String, location: String) {
    Column(modifier = Modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(icon = Icons.Filled.Phone, text = phoneNumber)
        InfoRow(icon = Icons.Filled.Email, text = email)
        InfoRow(icon = Icons.Filled.WatchLater, text = "Schedule: $operatingHours")
        InfoRow(icon = Icons.Filled.LocationOn, text = "Location: $location")
    }
}

@Composable
fun AboutUsSection(description:String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextSemiBold(text = "About us:")
        TextNormal(text = description, color = Color.Gray)
    }
}

fun capitalizeFirstLetter2(text: String): String {
    return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}



@Composable
fun InfoRow(icon: ImageVector?, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        TextNormal(text = text, color= Color.Gray)
    }
}


@Composable
fun VeterinaryCard(vet: Vet, navController: NavHostController) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F6FF),
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .width(100.dp)
            .clickable {
                navController.navigate(Routes.OwnerVetProfile.createRoute(vet.id))
            }
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                imageModel = { vet.imageUrl },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                imageOptions = ImageOptions(contentScale = ContentScale.Crop)
            )
            TextSemiBold(text = capitalizeFirstLetter(vet.name))
            //TextNormal(text = vet.name, color = Color.Black)
        }
    }
}
