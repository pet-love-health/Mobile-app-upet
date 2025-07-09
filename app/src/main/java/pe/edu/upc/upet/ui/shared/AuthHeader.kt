package pe.edu.upc.upet.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.upet.R
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.poppinsFamily

@Composable
fun AuthHeader(texto: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(
                top = 20.dp, bottom = 20.dp
            ).fillMaxWidth(),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSecondary,
                fontFamily = poppinsFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .height(223.dp)
                .width(217.dp)
                .shadow(30.dp, shape = RoundedCornerShape(200.dp), clip = false, ambientColor = Blue1, spotColor = Blue1),
            contentScale = ContentScale.Fit
        )
    }
}