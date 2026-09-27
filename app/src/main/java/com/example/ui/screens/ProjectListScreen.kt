package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextDrawProject
import com.example.ui.components.ImGuiButtonWidget
import com.example.ui.components.ImGuiCheckbox
import com.example.ui.components.ImGuiInputText
import com.example.ui.components.ImGuiWindow
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ImGuiBg
import com.example.ui.theme.ImGuiBorder
import com.example.ui.theme.ImGuiButton
import com.example.ui.theme.ImGuiCyanAccent
import com.example.ui.theme.ImGuiGoldAccent
import com.example.ui.theme.ImGuiMenuBarBg
import com.example.ui.theme.ImGuiRed
import com.example.ui.theme.ImGuiText
import com.example.ui.theme.ImGuiTextDisabled
import com.example.ui.theme.ImGuiTitleBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dear ImGui Project Hub & Launcher with Safe Insets
 */
@Composable
fun ProjectListScreen(
    projects: List<TextDrawProject>,
    onOpenProject: (TextDrawProject) -> Unit,
    onCreateProject: (String, Boolean) -> Unit,
    onDeleteProject: (Long) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newProjectName by remember { mutableStateOf("") }
    var isPlayerTextDraw by remember { mutableStateOf(false) }

    val insets = WindowInsets.systemBars.asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ImGuiBg)
            .padding(insets)
    ) {
        // Main ImGui Menu Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ImGuiMenuBarBg)
                .border(1.dp, ImGuiBorder)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SA-MP TextDraw Studio",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                ImGuiButtonWidget(
                    text = "+ New Project",
                    onClick = { showCreateDialog = true },
                    backgroundColor = GoldAccent,
                    textColor = Color.Black
                )
            }

            Text(
                text = "${projects.size} Projects • 640x480 Standard",
                color = ImGuiTextDisabled,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Workspace
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            ImGuiWindow(
                title = "Projects",
                modifier = Modifier.fillMaxSize(),
                isActive = true
            ) {
                if (projects.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No saved projects",
                                color = ImGuiTextDisabled,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ImGuiButtonWidget(
                                text = "Create Project",
                                onClick = { showCreateDialog = true },
                                backgroundColor = GoldAccent,
                                textColor = Color.Black
                            )
                        }
                    }
                } else {
                    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(projects, key = { it.id }) { project ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(ImGuiTitleBg)
                                    .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                                    .clickable { onOpenProject(project) }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(if (project.isPlayerTextDraw) ImGuiGoldAccent else ImGuiCyanAccent)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = project.name,
                                            color = ImGuiText,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${if (project.isPlayerTextDraw) "PlayerTextDraw" else "GlobalTextDraw"} | ${dateFormat.format(Date(project.updatedAt))}",
                                            color = ImGuiTextDisabled,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteProject(project.id) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ImGuiRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = ImGuiBg,
            title = {
                Text("New Project", color = GoldAccent, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImGuiInputText(
                        label = "Name",
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        placeholder = "e.g. LS_HUD_Speedometer"
                    )

                    ImGuiCheckbox(
                        label = "PlayerTextDraw (Per-player mode)",
                        checked = isPlayerTextDraw,
                        onCheckedChange = { isPlayerTextDraw = it }
                    )
                }
            },
            confirmButton = {
                ImGuiButtonWidget(
                    text = "Create",
                    onClick = {
                        onCreateProject(newProjectName.ifBlank { "Untitled TD" }, isPlayerTextDraw)
                        newProjectName = ""
                        showCreateDialog = false
                    },
                    backgroundColor = GoldAccent,
                    textColor = Color.Black
                )
            },
            dismissButton = {
                ImGuiButtonWidget(
                    text = "Cancel",
                    onClick = { showCreateDialog = false },
                    backgroundColor = ImGuiButton,
                    textColor = ImGuiText
                )
            }
        )
    }
}
