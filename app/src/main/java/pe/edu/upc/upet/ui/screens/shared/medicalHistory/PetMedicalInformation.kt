package pe.edu.upc.upet.ui.screens.shared.medicalHistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pe.edu.upc.upet.feature_medicalHistory.data.remote.DiseaseResponse
import pe.edu.upc.upet.feature_medicalHistory.data.remote.MedicalHistoryResponse
import pe.edu.upc.upet.feature_medicalHistory.data.remote.MedicalResultResponse
import pe.edu.upc.upet.feature_medicalHistory.data.remote.SurgeryResponse
import pe.edu.upc.upet.feature_medicalHistory.data.remote.VaccineResponse
import pe.edu.upc.upet.feature_medicalHistory.data.repository.MedicalHistoryRepository
import pe.edu.upc.upet.feature_pdf.data.remote.PdfService
import pe.edu.upc.upet.feature_pet.data.repository.PetRepository
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.isOwnerAuthenticated
import pe.edu.upc.upet.ui.shared.CustomButton
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.Pink

@Composable
fun PetMedicalInformation(navController: NavController, petId: Int) {
    val medicalHistoryRepository = MedicalHistoryRepository()
    var medicalHistory by remember { mutableStateOf<MedicalHistoryResponse?>(null) }
    var medicalResults by remember { mutableStateOf<List<MedicalResultResponse>?>(null) }
    var vaccines by remember { mutableStateOf<List<VaccineResponse>?>(null) }
    var diseases by remember { mutableStateOf<List<DiseaseResponse>?>(null) }
    var surgeries by remember { mutableStateOf<List<SurgeryResponse>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(petId) {
        medicalHistoryRepository.getMedicalHistoryByPetId(petId) { history ->
            medicalHistory = history
            history?.let {
                medicalHistoryRepository.getAllMedicalResultsByMedicalHistoryId(it.id) { results ->
                    medicalResults = results
                }
                medicalHistoryRepository.getAllVaccinesByMedicalHistoryId(it.id) { vaccinesList ->
                    vaccines = vaccinesList
                }
                medicalHistoryRepository.getAllDiseasesByMedicalHistoryId(it.id) { diseasesList ->
                    diseases = diseasesList
                }
                medicalHistoryRepository.getAllSurgeriesByMedicalHistoryId(it.id) { surgeriesList ->
                    surgeries = surgeriesList
                }
            }
            isLoading = false
        }
    }

    var selectedOption by remember { mutableStateOf(MedicalInfoOption.Diseases) }

    Scaffold(
        topBar = { TopBar(navController, "Medical Information") },
        floatingActionButton = {
            if(isOwnerAuthenticated()){

            } else {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.AddReport.createRoute( petId))
                    },
                    contentColor = Color.White,
                    containerColor = Pink,
                ) {
                    Icon(imageVector = Icons.Default.Add,
                        contentDescription = "Add")
                }
            }
        },
        content = { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                ) {
                    medicalHistory?.let { history ->
                        MedicalHistorySection(history, petId)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OptionSelector(selectedOption) { option ->
                        selectedOption = option
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedOption) {
                        MedicalInfoOption.MedicalResults -> MedicalResultsSection(medicalResults)
                        MedicalInfoOption.Vaccines -> VaccinesSection(vaccines)
                        MedicalInfoOption.Diseases -> DiseasesSection(diseases)
                        MedicalInfoOption.Surgeries -> SurgeriesSection(surgeries)
                    }
                }
            }
        }
    )
}

@Composable
fun MedicalHistorySection(history: MedicalHistoryResponse, petId: Int) {
    Column {
        Text("Medical History", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Date: ${history.date}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Description: ${history.description}", style = MaterialTheme.typography.bodyMedium)
        val uriHandler = LocalUriHandler.current
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Blue1),
            onClick = { uriHandler.openUri("https://web-production-4270c.up.railway.app/api/v1/pets/$petId/medical-report") }) {
            Text("Download Medical History")
        }
    }
}

@Composable
fun MedicalResultsSection(results: List<MedicalResultResponse>?) {
    results?.let {
        if (it.isEmpty()) {
            EmptyState("No medical results available")
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Medical Results", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(it) { result ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                                contentColor = Pink
                            ), modifier = Modifier.padding(8.dp).fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "Date: ${result.resultDate}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Type: ${result.resultType}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Description: ${result.description}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaccinesSection(vaccines: List<VaccineResponse>?) {
    vaccines?.let {
        if (it.isEmpty()) {
            EmptyState("No vaccines available")
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Vaccines", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(it) { vaccine ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                                contentColor = Pink
                            ), modifier = Modifier.padding(8.dp).fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "Name: ${vaccine.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Date: ${vaccine.vaccineDate}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Type: ${vaccine.vaccineType}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Dose: ${vaccine.dose}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Location: ${vaccine.location}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiseasesSection(diseases: List<DiseaseResponse>?) {
    diseases?.let {
        if (it.isEmpty()) {
            EmptyState("No diseases available")
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Diseases", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(it) { disease ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                                contentColor = Pink
                            ), modifier = Modifier.padding(8.dp).fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "Name: ${disease.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Diagnosis Date: ${disease.diagnosisDate}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Severity: ${disease.severity}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurgeriesSection(surgeries: List<SurgeryResponse>?) {
    surgeries?.let {
        if (it.isEmpty()) {
            EmptyState("No surgeries available")
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Surgeries", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(it) { surgery ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White,
                                contentColor = Pink
                            ), modifier = Modifier.padding(8.dp).fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "Date: ${surgery.surgeryDate}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Description: ${surgery.description}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionSelector(selectedOption: MedicalInfoOption, onOptionSelected: (MedicalInfoOption) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        MedicalInfoOption.entries.forEach { option ->
            TextButton(
                onClick = { onOptionSelected(option) },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (option == selectedOption) Color.White else Color.Transparent,
                )
            ) {
                Text(option.title, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

enum class MedicalInfoOption(val title: String) {
    MedicalResults("Medical Results"),
    Vaccines("Vaccines"),
    Diseases("Diseases"),
    Surgeries("Surgeries")
}
