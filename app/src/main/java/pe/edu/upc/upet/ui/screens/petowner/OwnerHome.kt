package pe.edu.upc.upet.ui.screens.petowner

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import pe.edu.upc.upet.feature_pet.data.repository.PetRepository
import pe.edu.upc.upet.feature_pet.domain.Pet
import pe.edu.upc.upet.feature_vetClinic.data.repository.VeterinaryClinicRepository
import pe.edu.upc.upet.feature_vetClinic.domain.VeterinaryClinic
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.vetclinic.VetClinicCard
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.ui.theme.PinkStrong
import pe.edu.upc.upet.utils.TokenManager.getUserIdAndRoleFromToken
import pe.edu.upc.upet.R
import pe.edu.upc.upet.ui.screens.petowner.vetclinic.capitalizeFirstLetter
import pe.edu.upc.upet.ui.theme.Pink

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OwnerHome(navController: NavController){
    Log.d("OwnerHome", "OwnerHome")
    Scaffold(modifier = Modifier) { paddingValues->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()

        ){
            LazyColumn(modifier = Modifier
                .fillMaxSize()

            ) {
                item { UserSection(navController) }
                item { NewsSection(description1 = "Monitorea la salud de tu mascota", description2 = "¡Cuida a tu mascota como nunca antes!" ) }
                item { PetsSection(navController) }
                item { RecommendedVetsSection(navController) }

            }
        }
    }
}

@Composable
fun NewsSection( description1: String, description2: String ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(BorderPadding).shadow(8.dp, RoundedCornerShape(12.dp))
            .background(Blue1, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Blue1
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column (
                modifier = Modifier.weight(1f).padding(10.dp)
            ){
                Text(
                    text = description1,//"Monitorea la salud de tu mascota",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description2,//"¡Cuida a tu mascota como nunca antes!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            GlideImage(modifier = Modifier
                .size(130.dp).weight(0.5f).align(Alignment.Bottom),
                imageModel = { R.drawable.pet },
                imageOptions = ImageOptions(contentScale = ContentScale.Crop)
            )
        }
    }
}



@Composable
fun UserSection(navController: NavController) {
    val owner = getOwner() ?: return
    Log.d("UserSection", "Owner: $owner")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlideImage(modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(50.dp))
                .border(1.dp, Pink, RoundedCornerShape(50.dp))
                .clickable { navController.navigate(Routes.OwnerProfile.route)},
                imageModel = { owner.imageUrl },
            )
            Column(modifier = Modifier.padding(start = 20.dp)) {
                val greeting = "Hello, ${capitalizeFirstLetter(owner.name)}"
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Blue1
                )
                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Blue1
                )
            }
        }
        IconButton(onClick = {
            navController.navigate(Routes.CreateNotification.route) }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Blue1
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PetsSection(navController: NavController) {
    val petRepository = remember { PetRepository() }
    var pets: List<Pet> by remember { mutableStateOf(emptyList()) }

    val ownerId = getUserIdAndRoleFromToken()?.first

    if (ownerId != null) {
        petRepository.getPetsByOwnerId(ownerId){
            pets = it
        }
    }

    Column {
        Row(
            modifier = Modifier
                .padding(BorderPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My Pets (${pets.size})",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Blue1
            )
            Text(
                text = "+ Add a pet",
                style = MaterialTheme.typography.bodyMedium,
                color = PinkStrong,
                modifier = Modifier
                    .clickable {  navController.navigate(Routes.RegisterPet.route) }
            )

        }
        LazyRow(modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            items(pets.take(4)) { pet ->
                SimplePetCard(pet) {
                    navController.navigate(Routes.PetDetails.createRoute(pet.id))
                }
            }
        }
    }
}



@Composable
fun RecommendedVetsSection(navController: NavController) {
    val vetClinicRepository = remember { VeterinaryClinicRepository() }
    var vetClinics: List<VeterinaryClinic> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(key1 = vetClinicRepository) {
        getUserIdAndRoleFromToken()?.first?.let {
            vetClinicRepository.getFavoriteVeterinaryClinics(it) { vetClinicsList ->
                vetClinics = vetClinicsList
            }
        }
    }

    Column {
        Text(
            text = "Favorite Clinics",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )
        Column {
            vetClinics.forEach { vetClinic ->
                VetClinicCard(navController,vetClinic)
                Spacer(modifier = Modifier.height(22.dp))
            }
        }
    }
}


@Composable
fun SimplePetCard(pet: Pet, onPetSelected: (Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable { onPetSelected(pet.id) }
    ) {
        Log.d("SimplePetCard", "SimplePetCard: ${pet.image_url}")

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlideImage(
                imageModel = { pet.image_url },
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp)),
                imageOptions = ImageOptions(contentScale = ContentScale.Crop)
            )
            Text(
                text = pet.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}