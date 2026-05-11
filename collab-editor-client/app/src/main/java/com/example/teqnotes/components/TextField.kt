package com.example.teqnotes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqnotes.R
import com.example.teqnotes.ui.theme.FiraCode

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: Int,
    placeholder: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val isFilled = value.isNotEmpty()

    val borderColor = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val iconColor = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val placeholderColor = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    val visualTransformation = if (isPassword && !isPasswordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                painterResource(id = leadingIcon),
                contentDescription = null
            )
        },
        trailingIcon = if (isPassword) {
            {
                Icon(
                    painterResource(
                        id = if (isPasswordVisible) R.drawable.sv_eye_on else R.drawable.sv_eye_off
                    ),
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                )
            }
        } else {
            null
        },
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = placeholderColor
                ),
                textAlign = TextAlign.Start
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = borderColor,

            cursorColor = MaterialTheme.colorScheme.primary,

            unfocusedPlaceholderColor = placeholderColor,
            focusedPlaceholderColor = MaterialTheme.colorScheme.primary,

            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = iconColor,

            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedTrailingIconColor = iconColor,

            errorTextColor = MaterialTheme.colorScheme.error,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLeadingIconColor = MaterialTheme.colorScheme.error,
            errorTrailingIconColor = MaterialTheme.colorScheme.error
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else {
            KeyboardOptions.Default
        },
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        isError = isError
    )
}