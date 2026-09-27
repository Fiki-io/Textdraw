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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.TextDrawElement
import com.example.ui.theme.ImGuiBorder
import com.example.ui.theme.ImGuiChildBg
import com.example.ui.theme.ImGuiCyanAccent
import com.example.ui.theme.ImGuiGoldAccent
import com.example.ui.theme.ImGuiHeader
import com.example.ui.theme.ImGuiRed
import com.example.ui.theme.ImGuiText
import com.example.ui.theme.ImGuiTextDisabled
import com.example.ui.theme.ImGuiTitleBg
import com.example.ui.theme.ImGuiTitleBgActive
import com.example.util.SampColorUtils

/**
 * ImGui Dedicated Outliner / Hierarchy Window Dialog
 */
@Composable
fun LayersManagerDialog(
    elements: List<TextDrawElement>,
    selectedElementId: String?,
    onDismiss: () -> Unit,
    onSelectElement: (String) -> Unit,
    onAddElement: () -> Unit,
    onAddSpriteElement: () -> Unit = {},
    onAddModelElement: () -> Unit = {},
    onDuplicateElement: (TextDrawElement) -> Unit,
    onDeleteElement: (String) -> Unit,
    onMoveZIndex: (Int, Int) -> Unit,
    onToggleLock: (String) -> Unit,
    onToggleVisibility: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .width(480.dp)
                .height(400.dp),
            colors = CardDefaults.cardColors(containerColor = ImGuiChildBg),
            shape = RoundedCornerShape(2.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ImGuiBorder))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ImGui Dialog Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ImGuiTitleBgActive)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ImGui::Window(\"Outliner - Layers Hierarchy (${elements.size})\")",
                        color = ImGuiText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = ImGuiTextDisabled, modifier = Modifier.size(14.dp))
                    }
                }

                // Sub Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ImGuiTitleBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total: ${elements.size} layers", color = ImGuiTextDisabled, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ImGuiButtonWidget(
                            text = "+ Text",
                            onClick = onAddElement,
                            backgroundColor = ImGuiCyanAccent,
                            textColor = Color.Black
                        )
                        ImGuiButtonWidget(
                            text = "+ Sprite",
                            onClick = onAddSpriteElement,
                            backgroundColor = ImGuiGoldAccent,
                            textColor = Color.Black
                        )
                        ImGuiButtonWidget(
                            text = "+ Model",
                            onClick = onAddModelElement,
                            backgroundColor = ImGuiCyanAccent,
                            textColor = Color.Black
                        )
                    }
                }

                val sortedList = elements.sortedByDescending { it.zIndex }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
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
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                    text = "F${item.font} | Pos: (${item.posX.toInt()}, ${item.posY.toInt()})",
                                    color = ImGuiTextDisabled,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Actions
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
}
