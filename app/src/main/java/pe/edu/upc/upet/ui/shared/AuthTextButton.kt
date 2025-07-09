package pe.edu.upc.upet.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.ui.theme.Pink
import pe.edu.upc.upet.ui.theme.poppinsFamily

@Composable
fun AuthTextButton(
    clickableText: String? = null,
    text: String = "",
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    onClickClickableText: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = BorderPadding, end = BorderPadding, bottom = 15.dp, top = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = arrangement
    ) {
        Text(
            text = if (text.isEmpty()) text else "$text ",
            style = textStyle(MaterialTheme.colorScheme.onTertiary)
        )
        clickableText?.let {
            ClickableText(
                text = AnnotatedString(it),
                onClick = {
                    onClickClickableText()
                },
                style = textStyle(Pink)
            )
        }
    }
}

fun textStyle(color: Color): TextStyle {
    return TextStyle(
        color = color,
        fontSize = 14.sp,
        fontFamily = poppinsFamily,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.End
    )
}