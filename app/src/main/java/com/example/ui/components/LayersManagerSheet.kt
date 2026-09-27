package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextDrawElement
import com.example.ui.theme.ImGuiBorder
import com.example.ui.theme.ImGuiButton
import com.example.ui.theme.ImGuiCyanAccent
import com.example.ui.theme.ImGuiGoldAccent
import com.example.ui.theme.ImGuiHeader
import com.example.ui.theme.ImGuiRed
import com.example.ui.theme.ImGuiText
import com.example.ui.theme.ImGuiTextDisabled
import com.example.ui.theme.ImGuiTitleBg
import com.example.util.SampColorUtils

/**
 * Dear ImGui Layer Outliner / Tree View
 */
@Composable
fun LayersManagerSheet(
    elements: List<TextDrawElement>,
    selectedElementId: String?,
    onSelectElement: (String) -> Unit,
    onAddElement: () -> Unit,
    onDuplicateElement: (TextDrawElement) -> Unit,
    onDeleteElement: (String) -> Unit,
    onMoveZIndex: (Int, Int) -> Unit,
    onToggleLock: (String) -> Unit,
    onToggleVisibility: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ImGuiWindow(
        title = "Outliner: Layers (${elements.size})",
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ImGuiTitleBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Z-Order Hierarchy", color = ImGuiTextDisabled, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                ImGuiButtonWidget(
                    text = "+ New Element",
                    onClick = onAddElement,
                    backgroundColor = ImGuiCyanAccent,
                    textColor = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val sortedList = elements.sortedByDescending { it.zIndex }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(sortedList) { index, item ->
                    val isSelected = item.id == selectedElementId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) ImGuiHeader else Color.Transparent)
                            .border(1.dp, if (isSelected) ImGuiCyanAccent else ImGuiBorder, RoundedCornerShape(2.dp))
                            .clickable { onSelectElement(item.id) }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color Tag
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(SampColorUtils.sampHexToComposeColor(item.color))
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.varName,
                                color = if (isSelected) ImGuiCyanAccent else ImGuiText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Font ${item.font} | Pos: (${item.posX.toInt()}, ${item.posY.toInt()})",
                                color = ImGuiTextDisabled,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // ImGui Compact Action Buttons
                        IconButton(
                            onClick = {
                                if (index > 0) {
                                    val other = sortedList[index - 1]
                                    onMoveZIndex(item.zIndex, other.zIndex)
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = ImGuiText, modifier = Modifier.size(14.dp))
                        }

                        IconButton(
                            onClick = {
                                if (index < sortedList.size - 1) {
                                    val other = sortedList[index + 1]
                                    onMoveZIndex(item.zIndex, other.zIndex)
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = ImGuiText, modifier = Modifier.size(14.dp))
                        }

                        IconButton(
                            onClick = { onDuplicateElement(item) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Clone", tint = ImGuiGoldAccent, modifier = Modifier.size(14.dp))
                        }

                        IconButton(
                            onClick = { onToggleLock(item.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (item.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Lock",
                                tint = if (item.isLocked) ImGuiGoldAccent else ImGuiTextDisabled,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        IconButton(
                            onClick = { onToggleVisibility(item.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (item.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Vis",
                                tint = if (item.isVisible) ImGuiCyanAccent else ImGuiTextDisabled,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteElement(item.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Del", tint = ImGuiRed, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
