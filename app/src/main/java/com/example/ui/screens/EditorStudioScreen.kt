package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextDrawElement
import com.example.model.TextDrawProject
import com.example.ui.components.CanvasNudgeControls
import com.example.ui.components.FloatingMovableInspector
import com.example.ui.components.LayersManagerDialog
import com.example.ui.components.ModelCatalogDialog
import com.example.ui.components.PawnExportDialog
import com.example.ui.components.PawnImportDialog
import com.example.ui.components.SampVirtualCanvas
import com.example.ui.components.SpriteCatalogDialog
import com.example.ui.theme.ButtonDark
import com.example.ui.theme.ButtonDarkBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ImGuiBg
import com.example.ui.theme.SampDarkBg
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.TextDrawViewModel
import kotlin.math.roundToInt

/**
 * Clean Studio Screen:
 * - Back confirmation dialog to prevent accidental exits!
 * - Scrollable & safe top bar
 * - Canvas with strict clipping (no overflow text)
 * - Micro D-Pad & Floating Inspector with full original features
 */
@Composable
fun EditorStudioScreen(
    project: TextDrawProject,
    viewModel: TextDrawViewModel,
    elements: List<TextDrawElement>,
    selectedElementId: String?,
    showGrid: Boolean,
    showGuides: Boolean,
    onBack: () -> Unit
) {
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    // Intercept back gesture so user doesn't accidentally exit the editor
    BackHandler {
        if (selectedElementId != null) {
            viewModel.selectElement(null) // Deselect first if an element is active
        } else {
            showExitConfirmDialog = true
        }
    }

    val selectedElement = elements.find { it.id == selectedElementId }

    // Dialogs
    var showLayersDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSpriteCatalog by remember { mutableStateOf(false) }
    var isCreatingNewSprite by remember { mutableStateOf(false) }
    var showModelCatalog by remember { mutableStateOf(false) }
    var isCreatingNewModel by remember { mutableStateOf(false) }

    // Floating Inspector Drag Coordinates
    var inspectorOffsetX by remember { mutableFloatStateOf(0f) }
    var inspectorOffsetY by remember { mutableFloatStateOf(0f) }

    val insets = WindowInsets.systemBars.asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SampDarkBg)
            .padding(insets)
    ) {
        // 1. Clean Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SampDarkBg)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[${project.name}]",
                color = GoldAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 6.dp)
            )

            SuiteButton(label = "< Back", onClick = { showExitConfirmDialog = true })
            SuiteButton(label = "+ Text", onClick = { viewModel.addNewElement() })
            SuiteButton(
                label = "+ Sprite",
                onClick = {
                    isCreatingNewSprite = true
                    showSpriteCatalog = true
                },
                isActive = true
            )
            SuiteButton(
                label = "+ 3D Model",
                onClick = {
                    isCreatingNewModel = true
                    showModelCatalog = true
                }
            )
            SuiteButton(
                label = "Outliner (${elements.size})",
                onClick = { showLayersDialog = true }
            )
            SuiteButton(label = "Undo", onClick = { viewModel.undo() })
            SuiteButton(label = "Redo", onClick = { viewModel.redo() })
            SuiteButton(
                label = if (showGrid) "Grid: ON" else "Grid: OFF",
                onClick = { viewModel.toggleGrid() }
            )
            SuiteButton(label = "Import .pwn", onClick = { showImportDialog = true })

            Spacer(modifier = Modifier.width(16.dp))

            // Golden EXPORT PWN Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(GoldAccent)
                    .clickable { showExportDialog = true }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EXPORT PWN",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. Main Workspace (Canvas, D-Pad, Draggable Inspector)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            // Virtual 640x480 Canvas (Full space)
            SampVirtualCanvas(
                elements = elements,
                selectedElementId = selectedElementId,
                showGrid = showGrid,
                showGuides = showGuides,
                onSelectElement = { viewModel.selectElement(it) },
                onElementMoved = { id, x, y -> viewModel.moveElement(id, x, y) },
                modifier = Modifier.fillMaxSize()
            )

            // D-Pad on Bottom-Left
            if (selectedElement != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 12.dp)
                ) {
                    CanvasNudgeControls(
                        element = selectedElement,
                        onNudge = { dx, dy -> viewModel.nudgeSelectedElement(dx, dy) }
                    )
                }
            }

            // Floating, Draggable Inspector Window (Only appears when element selected)
            if (selectedElement != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset { IntOffset(inspectorOffsetX.roundToInt(), inspectorOffsetY.roundToInt()) }
                        .padding(top = 10.dp, end = 12.dp)
                ) {
                    FloatingMovableInspector(
                        element = selectedElement,
                        onDrag = { dx, dy ->
                            inspectorOffsetX += dx
                            inspectorOffsetY += dy
                        },
                        onClose = { viewModel.selectElement(null) },
                        onElementChanged = { viewModel.updateElement(it) },
                        onOpenSpriteCatalog = {
                            isCreatingNewSprite = false
                            showSpriteCatalog = true
                        },
                        onOpenModelCatalog = {
                            isCreatingNewModel = false
                            showModelCatalog = true
                        }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            containerColor = ImGuiBg,
            title = {
                Text("Exit Studio?", color = GoldAccent, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Return to project list?", color = TextLight, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(GoldAccent)
                        .clickable {
                            showExitConfirmDialog = false
                            onBack()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Exit", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(ButtonDark)
                        .clickable { showExitConfirmDialog = false }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Cancel", color = TextLight, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }

    if (showLayersDialog) {
        LayersManagerDialog(
            elements = elements,
            selectedElementId = selectedElementId,
            onDismiss = { showLayersDialog = false },
            onSelectElement = {
                viewModel.selectElement(it)
                showLayersDialog = false
            },
            onAddElement = { viewModel.addNewElement() },
            onAddSpriteElement = {
                showLayersDialog = false
                isCreatingNewSprite = true
                showSpriteCatalog = true
            },
            onAddModelElement = {
                showLayersDialog = false
                isCreatingNewModel = true
                showModelCatalog = true
            },
            onDuplicateElement = { viewModel.duplicateElement(it) },
            onDeleteElement = { viewModel.deleteElement(it) },
            onMoveZIndex = { a, b -> viewModel.swapZIndex(a, b) },
            onToggleLock = { viewModel.toggleLock(it) },
            onToggleVisibility = { viewModel.toggleVisibility(it) }
        )
    }

    if (showExportDialog) {
        PawnExportDialog(
            project = project,
            elements = elements,
            onDismiss = { showExportDialog = false }
        )
    }

    if (showImportDialog) {
        PawnImportDialog(
            onDismiss = { showImportDialog = false },
            onImportParsedElements = { viewModel.importElements(it) }
        )
    }

    if (showSpriteCatalog) {
        SpriteCatalogDialog(
            onDismiss = {
                showSpriteCatalog = false
                isCreatingNewSprite = false
            },
            onSelectSprite = { spriteTag ->
                if (isCreatingNewSprite || selectedElement == null) {
                    viewModel.addNewSpriteElement(spriteTag)
                } else {
                    viewModel.updateElement(selectedElement.copy(text = spriteTag, font = 4))
                }
                showSpriteCatalog = false
                isCreatingNewSprite = false
            }
        )
    }

    if (showModelCatalog) {
        ModelCatalogDialog(
            onDismiss = {
                showModelCatalog = false
                isCreatingNewModel = false
            },
            onSelectModel = { modelId ->
                if (isCreatingNewModel || selectedElement == null) {
                    viewModel.addNewModelElement(modelId)
                } else {
                    viewModel.updateElement(selectedElement.copy(modelId = modelId, font = 5))
                }
                showModelCatalog = false
                isCreatingNewModel = false
            }
        )
    }
}

@Composable
private fun SuiteButton(
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (isActive) Color(0xFF283141) else ButtonDark)
            .border(1.dp, if (isActive) GoldAccent.copy(alpha = 0.6f) else ButtonDarkBorder, RoundedCornerShape(3.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isActive) GoldAccent else TextLight,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
