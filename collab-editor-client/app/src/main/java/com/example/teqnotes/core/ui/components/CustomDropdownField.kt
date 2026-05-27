package com.example.teqnotes.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.teqnotes.core.ui.theme.FiraCode
import com.example.teqnotes.R

@Composable
fun CustomDropdownField(
    label: String,
    leadingIcon: Int,
    isFocused: Boolean,
    selectedValue: String,
    onFocusedChange: (Boolean) -> Unit,
    onValueSelected: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    var dropdownOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    expanded = true
                    onFocusedChange(true)
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = null,
                tint = if (isFocused || selectedValue != "Выберите проект" && selectedValue != "Выберите друзей")
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(end = 16.dp)
            )

            Text(
                text = selectedValue,
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = if (isFocused || selectedValue != "Выберите проект" && selectedValue != "Выберите друзей")
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = if (expanded) R.drawable.sv_arrow_drop_up else R.drawable.sv_arrow_drop_down),
                contentDescription = "Expand",
                tint = if (isFocused || selectedValue != "Выберите проект" && selectedValue != "Выберите друзей")
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
        }

        if (expanded) {
            Popup(
                alignment = Alignment.BottomStart,
                offset = dropdownOffset,
                onDismissRequest = {
                    expanded = false
                    onFocusedChange(false)
                }
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .onGloballyPositioned { coordinates ->
                            val positionInWindow = coordinates.localToWindow(Offset.Zero)
                            dropdownOffset = IntOffset(
                                x = (positionInWindow.x + coordinates.size.width - 300).toInt(),
                                y = positionInWindow.y.toInt() - 170
                            )
                        }
                ) {
                    Column {
                        options.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onValueSelected(option)
                                        expanded = false
                                        onFocusedChange(false)
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = FiraCode,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            if (index < options.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}