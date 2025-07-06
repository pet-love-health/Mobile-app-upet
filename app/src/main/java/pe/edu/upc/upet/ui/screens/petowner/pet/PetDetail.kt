package pe.edu.upc.upet.ui.screens.petowner.pet

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.TagFaces
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import pe.edu.upc.upet.feature_pet.data.remote.GenderEnum
import pe.edu.upc.upet.feature_pet.data.repository.PetRepository
import pe.edu.upc.upet.feature_pet.domain.Pet
import pe.edu.upc.upet.feature_smartCollar.data.repository.SmartCollarRepository
import pe.edu.upc.upet.feature_smartCollar.domain.SmartCollar
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.getRole
import pe.edu.upc.upet.ui.shared.CustomButton
import pe.edu.upc.upet.ui.shared.CustomButton2
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.ui.theme.Pink
import pe.edu.upc.upet.ui.theme.poppinsFamily
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlin.coroutines.resume

@Composable
fun PetDetail(navController: NavHostController, petId: Int) {
    var pet by remember { mutableStateOf<Pet?>(null) }
    var isTracking by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var iconImage by remember { mutableStateOf(Icons.Filled.TagFaces) }
    var iconColor by remember { mutableStateOf(Color.Red) }
    var collarId by remember { mutableStateOf<Int?>(null) }
    var showMapDialog by remember { mutableStateOf(false) }
    val temperature = remember { mutableStateOf("0.0 °C") }
    val lpm = remember { mutableStateOf("0 lpm") }
    val battery = remember { mutableStateOf("0 %") }
    val gpsLatitude = remember { mutableStateOf<Double?>(null) }
    val gpsLongitude = remember { mutableStateOf<Double?>(null) }

    suspend fun getAllSmartCollarsSuspend(): List<SmartCollar> {
        return suspendCancellableCoroutine { continuation ->
            SmartCollarRepository().getAllSmartCollars { smartCollars ->
                continuation.resume(smartCollars)
            }
        }
    }

    suspend fun getCollarIdByProductCode(productCode: String): Int? {
        val collars = getAllSmartCollarsSuspend()
        return collars.firstOrNull { it.serial_number == productCode }?.id
    }

    fun fetchRealTimeData(petId: Int, onUpdate: (SmartCollar?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                SmartCollarRepository().getSmartCollarsByPetId(petId) { smartCollar ->
                    onUpdate(smartCollar)
                    isTracking = smartCollar != null
                }

                delay(1000)
            }
        }
    }


    PetRepository().getPetById(petId) {
        pet = it
    }

    val petValue = pet ?: return

    data class PetInfo(val title: String, val icon: ImageVector, val content: String)

    fun petResponseToPetInfoList(petResponse: Pet): List<PetInfo> {
        return listOf(
            PetInfo("Breed", Icons.Outlined.Pets, petResponse.breed),
            PetInfo("Species", Icons.Outlined.WbSunny, petResponse.specie),
            PetInfo("Weight", Icons.Outlined.Balance, petResponse.weight.toString()),
            PetInfo("Birthdate", Icons.Outlined.Timer, petResponse.birthdate),
        )
    }

    val petInfoList = petResponseToPetInfoList(petValue)


    LaunchedEffect(petValue.id) {
        fetchRealTimeData(petValue.id) { iotData ->
            if (iotData != null) {
                temperature.value = "${iotData.temperature} °C"
            }
            if (iotData != null) {
                lpm.value = "${iotData.lpm} lpm"
            }
            if (iotData != null) {
                battery.value = "${iotData.battery} %"
            }
            if (iotData != null) {
                gpsLatitude.value = iotData.location.latitude
            }
            if (iotData != null) {
                gpsLongitude.value = iotData.location.latitude
            }
        }
    }


    Scaffold(
        modifier = Modifier,
        topBar = {
            TopBarPet(
                navController = navController,
                title = petValue.name,
                gender = petValue.gender
            )
        },
    ) { paddingValues ->
        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                        .fillMaxSize()
                        .background(Color.White),
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    ImageRectangle(petValue.image_url)

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                            .background(Color(0xFFF0F6FF))
                    ) {
                        Column(modifier = Modifier.padding(15.dp, 15.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "General Information",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 24.sp,
                                    color = Color.Black,
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                )
                                IconButton(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    onClick = { }
                                ) {
                                    Icon(
                                        imageVector = iconImage,
                                        contentDescription = "TagFaces",
                                        tint = iconColor
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, bottom = BorderPadding)
                            ) {
                                petInfoList.forEach { petInfo ->
                                    val content =
                                        if (petInfo.title == "Weight") "${petInfo.content} k." else petInfo.content
                                    PetInformationCard(petInfo.title, petInfo.icon, content)
                                }
                            }

                            /*Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IoTInformationCard(
                                        title = "Temperature",
                                        icon = Icons.Outlined.Thermostat,
                                        content = temperature.value
                                    )
                                    IoTInformationCard(
                                        title = "LPM",
                                        icon = Icons.Outlined.Favorite,
                                        content = lpm.value
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IoTInformationCard(
                                        title = "Batery",
                                        icon = Icons.Outlined.BatteryChargingFull,
                                        content = battery.value
                                    )
                                    IoTInformationCard(
                                        title = "GPS (Click Me)",
                                        icon = Icons.Outlined.Map,
                                        content = if (gpsLatitude.value != null && gpsLongitude.value != null) {
                                            "${gpsLatitude.value}, ${gpsLongitude.value}"
                                        } else {
                                            "No disponible"
                                        },
                                        onClick = {
                                            if (gpsLatitude.value != null && gpsLongitude.value != null) {
                                                showMapDialog = true
                                            } else {
                                                Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                    if (showMapDialog) {
                                        MapDialog(
                                            onDismiss = { showMapDialog = false },
                                            latitude = gpsLatitude.value ?: 0.0,
                                            longitude = gpsLongitude.value ?: 0.0
                                        )
                                    }
                                }
                            }*/

                            Text(
                                text = "More details",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(top = 10.dp, bottom = 10.dp)
                            )

                            if (getRole() == "Vet") {
                                CustomButton(text = "Medical History") {
                                    navController.navigate(
                                        Routes.petMedicalHistory.createRoute(
                                            petValue.id
                                        )
                                    )
                                }
                            } else {
                                /*CustomButton2(
                                    text = if (isTracking) "Stop tracking" else "Start tracking",
                                    color = if (isTracking) Color.Red else Blue1,
                                    onClick = {
                                        if (isTracking) {
                                            collarId?.let { collarId ->
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    SmartCollarRepository().changePetAssociation(
                                                        collarId,
                                                        null
                                                    ) { success ->
                                                        if (success) {
                                                            isTracking = false
                                                            iconColor = Color.Red
                                                            iconImage = Icons.Filled.TagFaces
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            showDialog = true
                                        }
                                    }
                                )*/
                                if (showDialog) {
                                    TrackingDialog(
                                        onDismiss = { showDialog = false },
                                        onAccept = { code ->
                                            if (code.isNotBlank()) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    collarId =
                                                        getCollarIdByProductCode(code)
                                                    if (collarId != null) {
                                                        SmartCollarRepository().changePetAssociation(
                                                            collarId!!,
                                                            petId
                                                        ) { success ->
                                                            if (success) {
                                                                isTracking = true
                                                                iconColor = Color(0xFF09D809)
                                                                iconImage = Icons.Outlined.TagFaces
                                                                showDialog =
                                                                    false
                                                            } else {
                                                                isTracking = false
                                                                iconColor = Color.Red
                                                                iconImage = Icons.Filled.TagFaces
                                                            }
                                                        }
                                                    } else {
                                                        withContext(Dispatchers.Main) {
                                                            showDialog = false
                                                            Toast.makeText(
                                                                context,
                                                                "No se encontró el collar con ese código",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Por favor, ingrese un código válido",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    )
                                }

                                CustomButton(text = "Edit Profile") {
                                    navController.navigate(Routes.EditPetDetail.createRoute(petValue.id))
                                }

                                CustomButton(text = "Add Medical Information") {
                                    navController.navigate(Routes.AddReport.createRoute(petValue.id))
                                }
                                CustomButton(text = "Medical History") {
                                    navController.navigate(
                                        Routes.petMedicalHistory.createRoute(
                                            petValue.id
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TrackingDialog(
    onDismiss: () -> Unit,
    onAccept: (String) -> Unit,
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Enter the product code") },
        text = {
            Column {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Code") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAccept(code)
                }
            ) {
                Text("Accept")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun IoTInformationCard(
    title: String,
    icon: ImageVector,
    content: String,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .width(180.dp)
            .height(100.dp)
            .padding(6.dp)
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDAF1DB),
            contentColor = Color(0xFF0A2540)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Icon(icon, contentDescription = "IoT Icon", tint = Color(0xFF0A2540))
        }
        Text(
            text = content,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
fun ImageRectangle(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        GlideImage(
            modifier = Modifier
                .fillMaxWidth()
                .size(200.dp)
                .clip(shape = RoundedCornerShape(20.dp)),
            imageModel = { imageUrl })
    }
}

@Composable
fun PetInformationCard(title: String, icon: ImageVector, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F6FF),
            contentColor = Color(0xFF0A2540)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$title: ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Pink,
                modifier = Modifier.weight(0.5f)
            )
            Text(
                text = content,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)

            )

        }

    }
}
/*
@Composable
fun PetInformationCard(title:String, icon: ImageVector, content:String){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .width(180.dp)
            .height(100.dp)
            .padding(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color(0xFF0A2540)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Icon(icon, contentDescription = "heart", tint = Color(0xFF0A2540))
        }
        Text(
            text = content,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}*/

@Composable
fun MedicalHistoryCard(title: String, date: String, description: String, icon: ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
    {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0A2540),
                contentColor = Color.White
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFF6D6D), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Medical services",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = date,
                            fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarPet(
    navController: NavController,
    title: String,
    gender: GenderEnum
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = Color.Black,
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomReturnButton1(navController = navController)
                Text(
                    text = title,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (gender == GenderEnum.Male) Icons.Filled.Male else Icons.Filled.Female,
                    contentDescription = gender.toString(),
                    tint = if (gender == GenderEnum.Male) Blue1 else Pink
                )
            }
        }
    )
}

@Composable
fun CustomReturnButton1(navController: NavController) {
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(Pink)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            "Back",
            modifier = Modifier.fillMaxSize(1f),
            tint = Color.White
        )
    }
}

@Composable
fun MapDialog(
    onDismiss: () -> Unit,
    latitude: Double,
    longitude: Double
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubicación en el mapa") },
        text = {
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            ) {
                GoogleMapView(latitude = latitude, longitude = longitude)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun GoogleMapView(latitude: Double, longitude: Double) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)

    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    ) { mapView ->
        mapView.getMapAsync { googleMap ->
            googleMap.uiSettings.isZoomControlsEnabled = true
            val location = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(location).title("Ubicación actual"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(context: Context): MapView {
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) = mapView.onCreate(null)
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }
    return mapView
}
