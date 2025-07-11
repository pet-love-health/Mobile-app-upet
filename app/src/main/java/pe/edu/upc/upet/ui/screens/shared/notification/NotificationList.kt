package pe.edu.upc.upet.ui.screens.shared.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pe.edu.upc.upet.feature_notification.data.repository.NotificationRepository
import pe.edu.upc.upet.feature_notification.domain.Notification
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.vetclinic.capitalizeFirstLetter
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.utils.TokenManager

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationList(navController: NavHostController) {
    val (userId, role, _) = TokenManager.getUserIdAndRoleFromToken() ?: error("Error obteniendo el userId y userRole desde el token")
    val notificationRepository = remember { NotificationRepository() }
    var notifications: List<Notification> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(key1 = notificationRepository) {
        if (role == "Vet") {
            notificationRepository.getByVetId(userId) { notificationsList ->
                notifications = notificationsList
            }
        } else {
            notificationRepository.getByOwnerId(userId) { notificationsList ->
                notifications = notificationsList
            }
        }
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopBar(
                navController = navController,
                title = "Notifications"
            )
                 },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.CreateReminder.route)
                }
            ){

            }
        }
    ) { paddingValues->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                notifications.forEach { notification ->
                    NotificationCard(notification)
                    Spacer(modifier = Modifier.height(22.dp))
                }
            }
        }
    }

}

@Composable
fun NotificationCard(notification: Notification) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0F6FF),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(start = BorderPadding, end = BorderPadding)
    ) {
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = capitalizeFirstLetter(notification.title),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black
            )
            Column(modifier = Modifier.padding(top = 7.dp)) {
                Text(
                    text = notification.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.datetime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
