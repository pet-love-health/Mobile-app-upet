package pe.edu.upc.upet.ui.screens.shared.notification

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pe.edu.upc.upet.feature_notification.data.remote.ReminderRequest
import pe.edu.upc.upet.feature_notification.data.repository.NotificationRepository
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.screens.petowner.appointment.CalendarView
import pe.edu.upc.upet.ui.screens.petowner.appointment.MonthPicker
import pe.edu.upc.upet.ui.screens.shared.auth.aditionalInformation.shared.TimePickerInput
import pe.edu.upc.upet.ui.shared.AuthInputTextField
import pe.edu.upc.upet.ui.shared.CustomButton
import pe.edu.upc.upet.ui.shared.LabelTextField
import pe.edu.upc.upet.ui.shared.SuccessDialog
import pe.edu.upc.upet.ui.shared.TextSubtitle2
import pe.edu.upc.upet.ui.shared.TopBar
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.utils.TokenManager
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.Date

@Composable
fun CreateReminder(navController: NavHostController) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val dateTime = remember { mutableStateOf("") }
    val notificationRepository = NotificationRepository()

    val showSuccessDialog = remember { mutableStateOf(false) }
    if (showSuccessDialog.value) {
        SuccessDialog(onDismissRequest = {
            showSuccessDialog.value = false
            navController.navigate(Routes.CreateNotification.route)
        }, titleText = "Reminder Registered",
            messageText = "Your reminder has been registered successfully.",
            buttonText = "OK")
    }

    Scaffold(topBar = {
        TopBar(navController = navController, title = "Book Appointment")
    }, modifier = Modifier) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                item {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .padding(top = 10.dp, start = BorderPadding, end = BorderPadding),
                        verticalArrangement = Arrangement.Top
                    ) {
                        AuthInputTextField(
                            input = title,
                            placeholder = "Enter a title",
                            label = "Title",
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        AuthInputTextField(
                            input = description,
                            placeholder = "Enter a description",
                            label = "Description",
                        )

                        TextSubtitle2("Reminder Date")

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xA9F1DADC), RoundedCornerShape(15.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                MonthPicker(
                                    currentYearMonth = currentYearMonth,
                                    onYearMonthChange = { newYearMonth ->
                                        currentYearMonth = newYearMonth
                                    }
                                )

                                CalendarView(
                                    currentYearMonth = currentYearMonth,
                                    selectedDate = selectedDate,
                                    onDateSelected = { date ->
                                        selectedDate = date
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Row( modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            LabelTextField(label = "Reminder Time: ", commonPadding = 4.dp)
                            TimePickerInput(time = dateTime)
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        CustomButton(text = "Next", onClick = {
                            if (title.value.isNotEmpty() && description.value.isNotEmpty() && dateTime.value.isNotEmpty()) {
                                val reminderDateTime =
                                    LocalDate.of(
                                        selectedDate.year,
                                        selectedDate.monthValue,
                                        selectedDate.dayOfMonth
                                    ).atTime(
                                        LocalTime.parse(dateTime.value)
                                    ).toString()

                                var reminderRequest = TokenManager.getUserIdAndRoleFromToken()?.first?.let {
                                    ReminderRequest( // This will be set by the repository
                                        title = title.value,
                                        description = description.value,
                                        date_time = reminderDateTime,
                                        user_id = it,
                                    )

                                }
                                val user = TokenManager.getUserIdAndRoleFromToken()?.first
                                Log.d("CreateReminderScreen", title.value + " " + description.value + " " + user.toString() + " " + reminderDateTime)
                                // Create a new reminder
                                val reminder =
                                    reminderRequest?.let { it1 ->
                                        notificationRepository.createReminder(
                                            it1){ it2->
                                            if (it2) showSuccessDialog.value = true
                                            else Log.d("CreateReminderScreen", "Error creating reminder")
                                        }
                                    }

                                // Navigate to the next screen with the created reminder

                            }
                        })
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                }
            }
        }
    }


}