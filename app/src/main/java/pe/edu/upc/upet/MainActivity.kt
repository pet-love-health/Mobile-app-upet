package pe.edu.upc.upet

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cloudinary.android.MediaManager
import pe.edu.upc.upet.navigation.Navigation
import pe.edu.upc.upet.ui.theme.UpetTheme
import kotlin.math.exp

class MainActivity : ComponentActivity() {
    private var config: HashMap<String, String> = HashMap()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState )
        enableStrictMode()
        config["cloud_name"] = "dqgpis4fg"
        config["api_key"] = "824527285689877"
        config["api_secret"] = "GXHwHHEhNbEFOyPP0r6VuOQ84Dc"
        config["secure"] = "true"
        MediaManager.init(this, config)
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                darkTheme = isSystemInDarkTheme()
            } else {
                darkTheme = false // Fallback for older versions
            }
            UpetTheme(darkTheme = darkTheme) {
                val allLanguages = listOf(
                    Language("en", "English",R.drawable.upet),
                    Language("es", "Español",R.drawable.upet)
                )
                val currentLanguageCode:String = "en" // Default language code
                var currentLanguage by remember { mutableStateOf(currentLanguageCode) }
                val onLanguageSelected: (String) -> Unit = { languageCode ->
                    currentLanguage = languageCode
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation() // Update theme state
                }
            }
        }
    }
}

fun enableStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build()
    )
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
        }
    }

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

data class Language(
    val code: String,
    val name: String,
   @DrawableRes val flag: Int
)

@Composable
fun LanguageListItem(language: Language) {
    Image(
        modifier= Modifier
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

