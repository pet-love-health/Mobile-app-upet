package pe.edu.upc.upet.ui.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import pe.edu.upc.upet.ui.theme.Blue1

@Composable
fun CustomButton(text: String, icon: ImageVector? = null, onClick: () -> Unit, ) {
    Button(
        modifier = Modifier
            .fillMaxWidth()//.border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(10.dp)),
            .shadow(8.dp, shape = RoundedCornerShape(20.dp), ambientColor = Blue1, spotColor = Blue1),
                //.background(,shape = RoundedCornerShape(10.dp)),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Blue1)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = "$text  Icon")
        }
        Text(
            text = text,
        )
    }
}

@Composable
fun CustomButton2(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    color: Color = Blue1 // Color predeterminado
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = "$text Icon")
        }
        Text(text = text)
    }
}