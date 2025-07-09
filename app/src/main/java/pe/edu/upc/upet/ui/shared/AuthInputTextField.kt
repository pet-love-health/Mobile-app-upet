package pe.edu.upc.upet.ui.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.upet.ui.theme.Blue1
import pe.edu.upc.upet.ui.theme.BorderPadding
import pe.edu.upc.upet.ui.theme.Gray1
import pe.edu.upc.upet.ui.theme.Pink
import pe.edu.upc.upet.ui.theme.poppinsFamily

@Composable
fun AuthInputTextField(
    input: MutableState<String>,
    placeholder: String,
    label: String,
    type: TextFieldType = TextFieldType.Text,
    dropdownList: List<String>? = null
) {
    val commonPadding = BorderPadding
    val cornerSize = 10.dp

    LabelTextField(label, commonPadding)

    when (type) {
        TextFieldType.Password -> {
            CustomOutlinedTextFieldPassword(
                input = input,
                placeholder = placeholder,
                cornerSize = cornerSize
            )
        }
        TextFieldType.Dropdown -> {
            dropdownList?.let {
                DropdownTextField(
                    selectedItem = input,
                    cornerSize = cornerSize,
                    dropdownItems = it,
                    placeholder = placeholder
                )
            }
        }
        TextFieldType.Phone -> {
            CustomOutlinedTextFieldWithCountryCode(
                phoneNumber = input,
                placeholder = placeholder,
                cornerSize = cornerSize
            )
        }
        else -> {
            CustomOutlinedTextFieldNormal(
                input = input,
                placeholder = placeholder,
                cornerSize = cornerSize
            )
        }
    }
}

@Composable
fun LabelTextField(label: String, commonPadding: Dp){
    Text(
        text = label,
        modifier = Modifier.padding(
            start = commonPadding,
            end = commonPadding,
            bottom = 4.dp
        ),
        style = TextStyle(
            color = MaterialTheme.colorScheme.onTertiary,
            fontSize = 12.sp,
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
fun TextPlaceHolder(placeholder: String){
    Text(
        text = placeholder,
        style = TextStyle(
            color = Gray1,
            fontSize = 12.sp,
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal
        )
    )

}

enum class TextFieldType {
    Password,
    Phone,
    Dropdown,
    Text
}

@Composable
fun CustomOutlinedTextFieldNormal(
    input: MutableState<String>,
    placeholder: String,
    cornerSize: Dp
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = input.value,
        onValueChange = { input.value = it },
        placeholder = {
            TextPlaceHolder(placeholder)
        },
        shape = RoundedCornerShape(cornerSize),
        modifier = Modifier.commonModifier(cornerSize),
        textStyle = commonTextStyle(input.value),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        visualTransformation = VisualTransformation.None
    )
}

@Composable
fun CustomOutlinedTextFieldWithCountryCode(
    phoneNumber: MutableState<String>,
    placeholder: String,
    cornerSize: Dp
) {
    val focusManager = LocalFocusManager.current
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Campo de texto para el número de teléfono
        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            placeholder = {
                TextPlaceHolder(placeholder)
            },
            shape = RoundedCornerShape(cornerSize),
            modifier = Modifier.commonModifier(cornerSize),
            textStyle = commonTextStyle(phoneNumber.value),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            visualTransformation = VisualTransformation.None
        )
    }
}

@Composable
fun CustomOutlinedTextFieldPassword(
    input: MutableState<String>,
    placeholder: String,
    cornerSize: Dp
) {
    val isPasswordVisible = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = input.value,
        onValueChange = { input.value = it },
        placeholder = {
            TextPlaceHolder(placeholder)
        },
        shape = RoundedCornerShape(cornerSize),
        singleLine = true,
        modifier = Modifier
            .commonModifier(cornerSize),
        textStyle = commonTextStyle(input.value),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                Icon(
                    imageVector = if (isPasswordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (isPasswordVisible.value) "Hide password" else "Show password",
                    tint = Pink
                )
            }
        }
    )
}

fun Modifier.commonModifier(cornerSize: Dp, start: Dp = BorderPadding, end: Dp = BorderPadding) =
    this
        .fillMaxWidth()
        .size(height = 56.dp, width = 300.dp)
        .padding(bottom = 10.dp, start = start, end = end)
        .border(BorderStroke(2.dp, Pink), shape = RoundedCornerShape(cornerSize))
        .background(Color.White, shape = RoundedCornerShape(cornerSize))

fun commonTextStyle( input: String) = TextStyle(
    color = if (input.isNotEmpty()) Color.Black else Gray1,
    fontSize = 12.sp,
    fontFamily = poppinsFamily,
    fontWeight = FontWeight.Normal
)