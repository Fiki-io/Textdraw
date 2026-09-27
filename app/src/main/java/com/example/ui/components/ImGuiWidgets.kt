package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ImGuiBorder
import com.example.ui.theme.ImGuiButton
import com.example.ui.theme.ImGuiButtonActive
import com.example.ui.theme.ImGuiButtonHovered
import com.example.ui.theme.ImGuiCyanAccent
import com.example.ui.theme.ImGuiFrameBg
import com.example.ui.theme.ImGuiHeader
import com.example.ui.theme.ImGuiText
import com.example.ui.theme.ImGuiTextDisabled
import com.example.ui.theme.ImGuiTitleBg
import com.example.ui.theme.ImGuiTitleBgActive

/**
 * ImGui Window Container with 1px border and optional Title Bar
 */
@Composable
fun ImGuiWindow(
    title: String? = null,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
            .background(com.example.ui.theme.ImGuiChildBg, RoundedCornerShape(2.dp))
    ) {
        Column {
            if (title != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isActive) ImGuiTitleBgActive else ImGuiTitleBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = title,
                        color = ImGuiText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            content()
        }
    }
}

/**
 * ImGui DragFloat / DragInt Widget (Supports drag horizontal to increment or click to type)
 */
@Composable
fun ImGuiDragFloat(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    min: Float = 0f,
    max: Float = 640f,
    speed: Float = 1.0f,
    format: String = "%.1f"
) {
    var isEditing by remember { mutableStateOf(false) }
    var tempText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = ImGuiText,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(75.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(ImGuiFrameBg)
                .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                .pointerInput(value, isEditing) {
                    if (!isEditing) {
                        detectDragGestures(
                            onDragStart = { isEditing = false },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val nextVal = (value + dragAmount.x * speed * 0.25f).coerceIn(min, max)
                                onValueChange(nextVal)
                            }
                        )
                    }
                }
                .clickable {
                    if (!isEditing) {
                        tempText = format.format(value)
                        isEditing = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (isEditing) {
                BasicTextField(
                    value = tempText,
                    onValueChange = { tempText = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = ImGuiCyanAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    cursorBrush = SolidColor(ImGuiCyanAccent),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            tempText.toFloatOrNull()?.let { onValueChange(it.coerceIn(min, max)) }
                            isEditing = false
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            } else {
                Text(
                    text = format.format(value),
                    color = ImGuiCyanAccent,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * ImGui Text Input Widget
 */
@Composable
fun ImGuiInputText(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                color = ImGuiText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.width(75.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(26.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(ImGuiFrameBg)
                .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    color = ImGuiTextDisabled,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = ImGuiText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                ),
                cursorBrush = SolidColor(ImGuiCyanAccent),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * ImGui Checkbox Widget
 */
@Composable
fun ImGuiCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (checked) ImGuiButtonActive else ImGuiFrameBg)
                .border(1.dp, if (checked) ImGuiCyanAccent else ImGuiBorder, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = ImGuiCyanAccent,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = ImGuiText,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * ImGui Collapsing Header (Triangular fold)
 */
@Composable
fun ImGuiCollapsingHeader(
    label: String,
    defaultExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (expanded) ImGuiHeader else ImGuiTitleBg)
                .clickable { expanded = !expanded }
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowRight,
                contentDescription = null,
                tint = ImGuiText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = ImGuiText,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * ImGui Button (Flat compact styling)
 */
@Composable
fun ImGuiButtonWidget(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ImGuiButton,
    textColor: Color = ImGuiText
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(backgroundColor)
            .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold
        )
    }
}
