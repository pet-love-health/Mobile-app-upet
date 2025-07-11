package pe.edu.upc.upet.ui.screens.shared.auth.signin

import android.R.string
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pe.edu.upc.upet.R
import pe.edu.upc.upet.feature_auth.data.repository.AuthRepository
import pe.edu.upc.upet.navigation.Routes
import pe.edu.upc.upet.ui.shared.AuthButton
import pe.edu.upc.upet.ui.shared.AuthHeader
import pe.edu.upc.upet.ui.shared.AuthInputTextField
import pe.edu.upc.upet.ui.shared.AuthTextButton
import pe.edu.upc.upet.ui.shared.Dialog
import pe.edu.upc.upet.ui.shared.TextFieldType
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.BorderPadding
import java.lang.Double.isNaN
import kotlin.math.floor



@Composable
fun SignInScreen(authRepository: AuthRepository = AuthRepository(), navigateTo: (String) -> Unit){


    var captchaQuestion = remember { mutableStateOf("") }
    var captchaAnswer= remember { mutableStateOf("") }
    var expectedAnswer= remember { mutableStateOf(0) }
    var captchaError = remember { mutableStateOf("") }
    fun generateCaptcha() {
        val num1 = floor(Math.random() * 10).toInt()
        val num2 = floor(Math.random() * 10).toInt();
        captchaQuestion.value = "$num1 + $num2 = ?";
        expectedAnswer.value = num1 + num2;
        captchaAnswer.value = "";
        //this.captchaError = null;
    }
    fun validateCaptcha(): Boolean {
        val userAnswer = captchaAnswer.value.toDouble();
        if (isNaN(userAnswer) || userAnswer.toInt() !== expectedAnswer.value) {
            captchaError.value = "Respuesta incorrecta, intenta nuevamente";
            Log.d("error captcha", captchaError.value);
            generateCaptcha();
            return false;
        }
        captchaError.value = "";
        return true;
    }
    data class Language(
        val code: String,
        val name: String,
        @DrawableRes val flag: Int
    )
    val allLanguages = listOf(
        Language("en", "English", R.drawable.upet),
        Language("es", "Español", R.drawable.upet),
    )
    val currentLanguageCode:String = allLanguages.first().code ?: "en" // Default to English if null
    var currentLanguage by remember { mutableStateOf(currentLanguageCode) }
    val onLanguageChange: (String) -> Unit = { languageCode ->
        currentLanguage = languageCode
    }
    @Composable
    fun LanguageListItem(language: Language) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                painter = painterResource(id = language.flag),
                contentScale = ContentScale.Crop,
                contentDescription = language.code
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = language.name,
            )
        }
    }
    @Composable
    fun LanguageDropDown(
        languageList: List<Language>,
        modifier: Modifier,
        currentLanguage: String,
        onLanguageSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf(languageList.first {it.code ==currentLanguage}) }

        Box(
            modifier = modifier
                .padding(end = 16.dp)
                .wrapContentSize(Alignment.TopEnd)
        ){
            Row (
                modifier = Modifier
                    .height(24.dp)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                LanguageListItem(selectedItem)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ){
                    repeat(languageList.size) { index ->
                        val language = languageList[index]
                        DropdownMenuItem(
                            text = {
                                LanguageListItem(language=language)
                            }, onClick = {
                                selectedItem=language;
                                onLanguageSelected(language.code);
                                expanded = !expanded
                            }
                        )
                    }
                }
            }
        }
    }
    Scaffold(modifier = Modifier) { paddingValues->
        val email = remember{
            mutableStateOf("")
        }
        val password = remember{
            mutableStateOf("")
        }
        val showErrorSnackbar = remember { mutableStateOf(false) }
        val snackbarMessage = remember { mutableStateOf("") }
        generateCaptcha()


        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ){
            Box {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()

                ) {
                    LanguageDropDown(
                        languageList = allLanguages,
                        modifier = Modifier.padding(top = 8.dp),
                        currentLanguage = currentLanguage,
                    ) { languageCode ->
                        onLanguageChange(languageCode)
                        currentLanguage = languageCode // Regenerate captcha when language changes
                    }
                    if (currentLanguageCode == "en") {
                        AuthHeader(texto = stringResource(id= R.string.login))
                        HorizontalDivider(
                            modifier = Modifier.padding(BorderPadding),
                            thickness = 30.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                        AuthInputTextField(
                            input = email,
                            placeholder = "Enter your email",
                            label = "Email"
                        )
                        AuthInputTextField(
                            input = password,
                            placeholder = "Enter your password",
                            label = "Password",
                            type= TextFieldType.Password
                        )
                        AuthInputTextField(
                            input = captchaAnswer,
                            placeholder = "Enter your answer",
                            label = captchaQuestion.value,
                        )

                        AuthTextButton("Forgot Password?", arrangement = Arrangement.End,
                            onClickClickableText = {
                                navigateTo(Routes.SendEmail.route)
                            }
                        )
                        AuthButton(text = "Log In", onClick = {
                            if (email.value.isEmpty()) {
                                snackbarMessage.value = "You must enter your email."
                                showErrorSnackbar.value = true
                            } else if(!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()){
                                snackbarMessage.value = "You must enter a valid email."
                                showErrorSnackbar.value = true
                            } else if (password.value.isEmpty()) {
                                snackbarMessage.value = "You must enter your password."
                                showErrorSnackbar.value = true
                            } else {
                                if(validateCaptcha()){
                                    authRepository.signIn( email.value, password.value) { success ->
                                        if (success) {
                                            Log.d("SuccesSignIn", "User authenticated")
                                            navigateTo(Routes.PostRegister.route)
                                        } else {
                                            snackbarMessage.value = "Invalid credentials."
                                            showErrorSnackbar.value = true
                                        }
                                    }
                                }
                                else {snackbarMessage.value == captchaError.value
                                    showErrorSnackbar.value = true}

                            }
                        })
                        HorizontalDivider(
                            modifier = Modifier.padding(BorderPadding),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        AuthTextButton(
                            "Register Now",
                            text = "New member?",
                            onClickClickableText = {
                                navigateTo(Routes.SignUp.route)
                            },
                        )
                    } else {
                        AuthHeader(texto = stringResource(id= R.string.login))
                        HorizontalDivider(
                            modifier = Modifier.padding(BorderPadding),
                            thickness = 30.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                        AuthInputTextField(
                            input = email,
                            placeholder = "Ingresa e-mail",
                            label = "E-mail"
                        )
                        AuthInputTextField(
                            input = password,
                            placeholder = "Ingresa contraseña",
                            label = "Contraseña",
                            type= TextFieldType.Password
                        )
                        AuthInputTextField(
                            input = captchaAnswer,
                            placeholder = "Ingresa tu respuesta",
                            label = captchaQuestion.value,
                        )

                        AuthTextButton("Olvidó su contraseña?", arrangement = Arrangement.End,
                            onClickClickableText = {
                                navigateTo(Routes.SendEmail.route)
                            }
                        )
                        AuthButton(text = "Ingresar", onClick = {
                            if (email.value.isEmpty()) {
                                snackbarMessage.value = "You must enter your email."
                                showErrorSnackbar.value = true
                            } else if(!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()){
                                snackbarMessage.value = "You must enter a valid email."
                                showErrorSnackbar.value = true
                            } else if (password.value.isEmpty()) {
                                snackbarMessage.value = "You must enter your password."
                                showErrorSnackbar.value = true
                            } else {
                                if(validateCaptcha()){
                                    authRepository.signIn( email.value, password.value) { success ->
                                        if (success) {
                                            Log.d("SuccesSignIn", "User authenticated")
                                            navigateTo(Routes.PostRegister.route)
                                        } else {
                                            snackbarMessage.value = "Invalid credentials."
                                            showErrorSnackbar.value = true
                                        }
                                    }
                                }
                                else {snackbarMessage.value == captchaError.value
                                    showErrorSnackbar.value = true}

                            }
                        })
                        HorizontalDivider(
                            modifier = Modifier.padding(BorderPadding),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        AuthTextButton(
                            "Registrarse ahora",
                            text = "Nuevo miembro?",
                            onClickClickableText = {
                                navigateTo(Routes.SignUp.route)
                            },
                        )
                    }

                }
                Dialog(message = (snackbarMessage.value), showError = showErrorSnackbar )
            }
        }
    }

}



