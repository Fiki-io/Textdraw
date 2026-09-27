package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextDrawElement
import com.example.ui.theme.ImGuiBorder
import com.example.ui.theme.ImGuiButton
import com.example.ui.theme.ImGuiChildBg
import com.example.ui.theme.ImGuiCyanAccent
import com.example.ui.theme.ImGuiFrameBg
import com.example.ui.theme.ImGuiText
import com.example.ui.theme.ImGuiTitleBgActive

/**
 * Compact Floating ImGui D-Pad Overlay (Muncul di Kiri Bawah Kanvas saat Elemen Terpilih)
 */
@Composable
fun CanvasNudgeControls(
    element: TextDrawElement?,
    onNudge: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    if (element == null) return

    var step by remember { mutableFloatStateOf(1.0f) }
    val steps = listOf(0.5f, 1.0f, 5.0f, 10.0f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(ImGuiChildBg.copy(alpha = 0.92f))
            .border(1.dp, ImGuiBorder, RoundedCornerShape(3.dp))
            .padding(6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Mini Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "D-Pad: ${element.varName}",
                    color = ImGuiCyanAccent,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "X: %.1f | Y: %.1f".format(element.posX, element.posY),
                color = ImGuiText,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Step selection buttons
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                steps.forEach { s ->
                    val isSel = step == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSel) ImGuiCyanAccent else ImGuiButton)
                            .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                            .clickable { step = s }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (s < 1f) ".5" else "${s.toInt()}",
                            color = if (isSel) Color.Black else ImGuiText,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Directional Arrow Keys
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ImGuiButton)
                    .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                    .clickable { onNudge(0f, -step) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = ImGuiText, modifier = Modifier.size(16.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ImGuiButton)
                        .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                        .clickable { onNudge(-step, 0f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = ImGuiText, modifier = Modifier.size(16.dp))
                }

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(ImGuiFrameBg)
                        .border(1.dp, ImGuiBorder, RoundedCornerShape(1.dp))
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ImGuiButton)
                        .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                        .clickable { onNudge(step, 0f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = ImGuiText, modifier = Modifier.size(16.dp))
                }
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ImGuiButton)
                    .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                    .clickable { onNudge(0f, step) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = ImGuiText)
            }
        }
    }
}
