package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.TextDrawElement
import com.example.ui.theme.CyanSelection
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ImGuiBorder
import com.example.ui.theme.ImGuiButton
import com.example.ui.theme.ImGuiCyanAccent
import com.example.ui.theme.ImGuiGoldAccent
import com.example.ui.theme.ImGuiHeader
import com.example.ui.theme.ImGuiText
import com.example.ui.theme.ImGuiTextDisabled
import com.example.ui.theme.SampBlue
import com.example.ui.theme.SampFontFamilies
import com.example.ui.theme.SampGreen
import com.example.ui.theme.SampPurple
import com.example.ui.theme.SampRed
import com.example.ui.theme.SampWhite
import com.example.ui.theme.SampYellow
import com.example.util.SampAssetCatalog
import com.example.util.SampColorUtils

/**
 * Context-Aware Element Inspector:
 * Shows ONLY the controls and properties that genuinely apply to the selected element type.
 * - Text Element (Font 0-3): Typography, LetterSize, Alignment, Outline, Shadow, Box
 * - Sprite Element (Font 4): Texture Tag, Catalog, Width/Height, Tint Color
 * - 3D Model Element (Font 5): Model ID, Catalog, Live Preview, Frame Size, 3D Rotations, Zoom, VehCol
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ElementInspectorPanel(
    element: TextDrawElement,
    onElementChanged: (TextDrawElement) -> Unit,
    onOpenSpriteCatalog: () -> Unit,
    onOpenModelCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(2.dp)) {

        // 1. Element Type Badge & Quick Converter
        ElementTypeHeader(
            element = element,
            onTypeChanged = { newFont ->
                val updated = when (newFont) {
                    4 -> element.copy(
                        font = 4,
                        text = if (element.text.contains(":")) element.text else "hud:radar_light",
                        textSizeX = if (element.textSizeX <= 1f) 40f else element.textSizeX,
                        textSizeY = if (element.textSizeY <= 1f) 40f else element.textSizeY
                    )
                    5 -> element.copy(
                        font = 5,
                        modelId = if (element.modelId > 0) element.modelId else 411,
                        textSizeX = if (element.textSizeX <= 1f) 65f else element.textSizeX,
                        textSizeY = if (element.textSizeY <= 1f) 65f else element.textSizeY
                    )
                    else -> element.copy(
                        font = newFont,
                        text = if (element.text.contains(":") || element.text.isBlank()) "GTA San Andreas" else element.text
                    )
                }
                onElementChanged(updated)
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 2. Specialized Inspector Based on Element Type
        when {
            element.font == 4 -> {
                // SPRITE TEXTURE INSPECTOR
                SpriteElementInspector(
                    element = element,
                    onElementChanged = onElementChanged,
                    onOpenSpriteCatalog = onOpenSpriteCatalog
                )
            }
            element.font == 5 -> {
                // 3D PREVIEW MODEL INSPECTOR
                ModelElementInspector(
                    element = element,
                    onElementChanged = onElementChanged,
                    onOpenModelCatalog = onOpenModelCatalog
                )
            }
            else -> {
                // STANDARD TEXT ELEMENT INSPECTOR (Font 0, 1, 2, 3)
                TextElementInspector(
                    element = element,
                    onElementChanged = onElementChanged
                )
            }
        }
    }
}

/**
 * Top Type Header Badge with quick convert buttons
 */
@Composable
private fun ElementTypeHeader(
    element: TextDrawElement,
    onTypeChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(ImGuiHeader)
            .border(1.dp, ImGuiBorder, RoundedCornerShape(3.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (typeName, typeColor) = when (element.font) {
            4 -> "SPRITE TEXTURE" to CyanSelection
            5 -> "3D PREVIEW MODEL" to GoldAccent
            else -> "TEXT (FONT ${element.font})" to Color(0xFF68D391)
        }

        Text(
            text = "TYPE: $typeName",
            color = typeColor,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            val isText = element.font in 0..3
            val isSprite = element.font == 4
            val isModel = element.font == 5

            TypeSwitchChip(label = "TXT", isSelected = isText, onClick = { if (!isText) onTypeChanged(2) })
            TypeSwitchChip(label = "SPR", isSelected = isSprite, onClick = { if (!isSprite) onTypeChanged(4) })
            TypeSwitchChip(label = "3D", isSelected = isModel, onClick = { if (!isModel) onTypeChanged(5) })
        }
    }
}

@Composable
private fun TypeSwitchChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isSelected) ImGuiCyanAccent else ImGuiButton)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else ImGuiTextDisabled,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

/**
 * 1. TEXT ELEMENT INSPECTOR (Font 0, 1, 2, 3)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TextElementInspector(
    element: TextDrawElement,
    onElementChanged: (TextDrawElement) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

        // Section: Variable & Content
        ImGuiCollapsingHeader(label = "Text Content & Identity") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiInputText(
                    label = "Variable",
                    value = element.varName,
                    onValueChange = { clean ->
                        onElementChanged(element.copy(varName = clean.filter { it.isLetterOrDigit() || it == '_' }))
                    }
                )

                ImGuiInputText(
                    label = "Content",
                    value = element.text,
                    onValueChange = { onElementChanged(element.copy(text = it)) }
                )

                // Quick SA-MP Color Tag Inserter Pills
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tag:", color = ImGuiTextDisabled, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf(
                            "~r~" to SampRed,
                            "~g~" to SampGreen,
                            "~b~" to SampBlue,
                            "~y~" to SampYellow,
                            "~w~" to SampWhite,
                            "~p~" to SampPurple
                        ).forEach { (tag, col) ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(col)
                                    .clickable {
                                        onElementChanged(element.copy(text = element.text + tag))
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Section: Position & Font Scale
        ImGuiCollapsingHeader(label = "Transform (Position & Scale)") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiDragFloat(
                    label = "Pos X",
                    value = element.posX,
                    onValueChange = { onElementChanged(element.copy(posX = it)) },
                    min = 0f,
                    max = 640f,
                    speed = 1.0f
                )
                ImGuiDragFloat(
                    label = "Pos Y",
                    value = element.posY,
                    onValueChange = { onElementChanged(element.copy(posY = it)) },
                    min = 0f,
                    max = 480f,
                    speed = 1.0f
                )

                Spacer(modifier = Modifier.height(2.dp))

                ImGuiDragFloat(
                    label = "Letter X",
                    value = element.letterSizeX,
                    onValueChange = { onElementChanged(element.copy(letterSizeX = it)) },
                    min = 0.05f,
                    max = 3.0f,
                    speed = 0.05f,
                    format = "%.3f"
                )
                ImGuiDragFloat(
                    label = "Letter Y",
                    value = element.letterSizeY,
                    onValueChange = { onElementChanged(element.copy(letterSizeY = it)) },
                    min = 0.1f,
                    max = 5.0f,
                    speed = 0.05f,
                    format = "%.3f"
                )
            }
        }

        // Section: Typography & Alignment
        ImGuiCollapsingHeader(label = "Typography & Style") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Font Style:", color = ImGuiTextDisabled, fontSize = 9.sp, fontFamily = FontFamily.Monospace)

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (0..3).forEach { f ->
                        val isSel = element.font == f
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isSel) ImGuiCyanAccent else ImGuiButton)
                                .clickable { onElementChanged(element.copy(font = f)) }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "F$f: ${SampFontFamilies.getFontName(f).substringAfter(": ")}",
                                color = if (isSel) Color.Black else ImGuiText,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Alignment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Align", color = ImGuiText, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(1 to "Left", 2 to "Center", 3 to "Right").forEach { (alignVal, label) ->
                            val sel = element.alignment == alignVal
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (sel) ImGuiCyanAccent else ImGuiButton)
                                    .clickable { onElementChanged(element.copy(alignment = alignVal)) }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (sel) Color.Black else ImGuiText,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                ImGuiCheckbox(
                    label = "Proportional Spacing",
                    checked = element.proportional,
                    onCheckedChange = { onElementChanged(element.copy(proportional = it)) }
                )
            }
        }

        // Section: Colors & Effects
        ImGuiCollapsingHeader(label = "Color & Effects") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Text Color
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Color: ${SampColorUtils.formatToSampPawnHex(element.color)}",
                        color = ImGuiText,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SampColorUtils.sampHexToComposeColor(element.color))
                            .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SampColorUtils.gtaColors.take(8).forEach { preset ->
                        val c = SampColorUtils.sampHexToComposeColor(preset.hex)
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(c)
                                .border(
                                    if (element.color == preset.hex) 1.5.dp else 1.dp,
                                    if (element.color == preset.hex) ImGuiCyanAccent else ImGuiBorder,
                                    RoundedCornerShape(2.dp)
                                )
                                .clickable { onElementChanged(element.copy(color = preset.hex)) }
                        )
                    }
                }

                ImGuiDragFloat(
                    label = "Outline",
                    value = element.outline.toFloat(),
                    onValueChange = { onElementChanged(element.copy(outline = it.toInt())) },
                    min = 0f,
                    max = 5f,
                    speed = 1f,
                    format = "%.0f"
                )
                ImGuiDragFloat(
                    label = "Shadow",
                    value = element.shadow.toFloat(),
                    onValueChange = { onElementChanged(element.copy(shadow = it.toInt())) },
                    min = 0f,
                    max = 5f,
                    speed = 1f,
                    format = "%.0f"
                )
            }
        }

        // Section: Optional Background Box
        ImGuiCollapsingHeader(label = "Background Box (UseBox)") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiCheckbox(
                    label = "Enable UseBox",
                    checked = element.useBox,
                    onCheckedChange = { onElementChanged(element.copy(useBox = it)) }
                )

                if (element.useBox) {
                    ImGuiDragFloat(
                        label = "Box Width",
                        value = element.textSizeX,
                        onValueChange = { onElementChanged(element.copy(textSizeX = it)) },
                        min = 1f,
                        max = 640f,
                        speed = 1.0f
                    )
                    ImGuiDragFloat(
                        label = "Box Height",
                        value = element.textSizeY,
                        onValueChange = { onElementChanged(element.copy(textSizeY = it)) },
                        min = 1f,
                        max = 480f,
                        speed = 1.0f
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BoxColor: ${SampColorUtils.formatToSampPawnHex(element.boxColor)}",
                            color = ImGuiGoldAccent,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SampColorUtils.sampHexToComposeColor(element.boxColor))
                                .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SampColorUtils.gtaColors.takeLast(6).forEach { preset ->
                            val c = SampColorUtils.sampHexToComposeColor(preset.hex)
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(c)
                                    .border(
                                        if (element.boxColor == preset.hex) 1.5.dp else 1.dp,
                                        if (element.boxColor == preset.hex) ImGuiGoldAccent else ImGuiBorder,
                                        RoundedCornerShape(2.dp)
                                    )
                                    .clickable { onElementChanged(element.copy(boxColor = preset.hex)) }
                            )
                        }
                    }
                }
            }
        }

        // Section: Properties
        ImGuiCollapsingHeader(label = "Interactive Properties") {
            ImGuiCheckbox(
                label = "Selectable (Clickable)",
                checked = element.selectable,
                onCheckedChange = { onElementChanged(element.copy(selectable = it)) }
            )
        }
    }
}

/**
 * 2. SPRITE ELEMENT INSPECTOR (Font 4)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SpriteElementInspector(
    element: TextDrawElement,
    onElementChanged: (TextDrawElement) -> Unit,
    onOpenSpriteCatalog: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

        // Section: Sprite Texture Asset
        ImGuiCollapsingHeader(label = "Sprite Texture (open.mp)") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiInputText(
                    label = "Variable",
                    value = element.varName,
                    onValueChange = { clean ->
                        onElementChanged(element.copy(varName = clean.filter { it.isLetterOrDigit() || it == '_' }))
                    }
                )

                ImGuiInputText(
                    label = "Texture",
                    value = element.text,
                    onValueChange = { onElementChanged(element.copy(text = it)) }
                )

                // Live Sprite Preview Thumbnail Box
                val spriteUrl = SampAssetCatalog.getSpriteImageUrl(element.text)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ImGuiButton.copy(alpha = 0.4f))
                        .border(1.dp, ImGuiBorder, RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (spriteUrl.isNotEmpty()) {
                        AsyncImage(
                            model = spriteUrl,
                            contentDescription = element.text,
                            modifier = Modifier.size(54.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = run {
                                val c = SampColorUtils.sampHexToComposeColor(element.color)
                                if (c != Color.White && c.alpha > 0.05f) {
                                    androidx.compose.ui.graphics.ColorFilter.tint(c, blendMode = androidx.compose.ui.graphics.BlendMode.Modulate)
                                } else null
                            }
                        )
                    }
                }

                ImGuiButtonWidget(
                    text = "Browse Sprite Catalog (470+)",
                    onClick = onOpenSpriteCatalog,
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = ImGuiCyanAccent,
                    textColor = Color.Black
                )
            }
        }

        // Section: Position & Size
        ImGuiCollapsingHeader(label = "Transform (Position & Size)") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // SA-MP: posX = left edge, posY = top edge
                ImGuiDragFloat(
                    label = "Pos X",
                    value = element.posX,
                    onValueChange = { newPosX ->
                        // Keep same visual width: endX shifts with posX
                        val currentW = (element.textSizeX - element.posX).coerceAtLeast(4f)
                        onElementChanged(element.copy(posX = newPosX, textSizeX = newPosX + currentW))
                    },
                    min = 0f,
                    max = 640f,
                    speed = 1.0f
                )
                ImGuiDragFloat(
                    label = "Pos Y",
                    value = element.posY,
                    onValueChange = { newPosY ->
                        val currentH = (element.textSizeY - element.posY).coerceAtLeast(4f)
                        onElementChanged(element.copy(posY = newPosY, textSizeY = newPosY + currentH))
                    },
                    min = 0f,
                    max = 480f,
                    speed = 1.0f
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Display width/height (computed from endX - posX)
                val displayWidth  = (element.textSizeX - element.posX).coerceAtLeast(0f)
                val displayHeight = (element.textSizeY - element.posY).coerceAtLeast(0f)

                ImGuiDragFloat(
                    label = "Width",
                    value = displayWidth,
                    onValueChange = { newW ->
                        // textSizeX = posX + newWidth (end-X in SA-MP coords)
                        onElementChanged(element.copy(textSizeX = element.posX + newW.coerceAtLeast(4f)))
                    },
                    min = 4f,
                    max = 640f,
                    speed = 1.0f
                )
                ImGuiDragFloat(
                    label = "Height",
                    value = displayHeight,
                    onValueChange = { newH ->
                        onElementChanged(element.copy(textSizeY = element.posY + newH.coerceAtLeast(4f)))
                    },
                    min = 4f,
                    max = 480f,
                    speed = 1.0f
                )

                // Quick Size Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "30x30" to (30f to 30f),
                        "40x40" to (40f to 40f),
                        "80x25" to (80f to 25f),
                        "120x35" to (120f to 35f)
                    ).forEach { (label, dims) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(ImGuiButton)
                                .clickable {
                                    // Store as endX/endY
                                    onElementChanged(element.copy(
                                        textSizeX = element.posX + dims.first,
                                        textSizeY = element.posY + dims.second
                                    ))
                                }
                                .padding(vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = ImGuiText, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Section: Color & Tint
        ImGuiCollapsingHeader(label = "Color & Texture Tint") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tint: ${SampColorUtils.formatToSampPawnHex(element.color)}",
                        color = ImGuiText,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SampColorUtils.sampHexToComposeColor(element.color))
                            .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SampColorUtils.gtaColors.take(8).forEach { preset ->
                        val c = SampColorUtils.sampHexToComposeColor(preset.hex)
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(c)
                                .border(
                                    if (element.color == preset.hex) 1.5.dp else 1.dp,
                                    if (element.color == preset.hex) ImGuiCyanAccent else ImGuiBorder,
                                    RoundedCornerShape(2.dp)
                                )
                                .clickable { onElementChanged(element.copy(color = preset.hex)) }
                        )
                    }
                }
            }
        }

        // Section: Interactive
        ImGuiCollapsingHeader(label = "Interactive Properties") {
            ImGuiCheckbox(
                label = "Selectable (Clickable Button)",
                checked = element.selectable,
                onCheckedChange = { onElementChanged(element.copy(selectable = it)) }
            )
        }
    }
}

/**
 * 3. 3D PREVIEW MODEL INSPECTOR (Font 5)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModelElementInspector(
    element: TextDrawElement,
    onElementChanged: (TextDrawElement) -> Unit,
    onOpenModelCatalog: () -> Unit
) {
    val isVehicle = element.modelId in 400..611
    val isSkin = element.modelId in 0..311

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

        // Section: 3D Model Asset
        ImGuiCollapsingHeader(label = "3D Model Asset") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiInputText(
                    label = "Variable",
                    value = element.varName,
                    onValueChange = { clean ->
                        onElementChanged(element.copy(varName = clean.filter { it.isLetterOrDigit() || it == '_' }))
                    }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ImGuiDragFloat(
                        label = "Model ID",
                        value = element.modelId.toFloat(),
                        onValueChange = { onElementChanged(element.copy(modelId = it.toInt())) },
                        min = 0f,
                        max = 20000f,
                        speed = 1f,
                        format = "%.0f",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    ImGuiButtonWidget(
                        text = "Catalog",
                        onClick = onOpenModelCatalog,
                        backgroundColor = ImGuiGoldAccent,
                        textColor = Color.Black
                    )
                }

                // Live Model Preview Frame
                val modelImgUrl = SampAssetCatalog.get3DModelImageUrl(element.modelId)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ImGuiButton.copy(alpha = 0.4f))
                        .border(1.dp, ImGuiBorder, RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = modelImgUrl,
                        contentDescription = "Model ${element.modelId}",
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = if (isVehicle) "Category: GTA Vehicle (ID: ${element.modelId})"
                    else if (isSkin) "Category: GTA Skin / Ped (ID: ${element.modelId})"
                    else "Category: GTA Object / Asset (ID: ${element.modelId})",
                    color = ImGuiTextDisabled,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Section: Position & Frame Size
        ImGuiCollapsingHeader(label = "Transform (Position & Frame)") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiDragFloat(
                    label = "Pos X",
                    value = element.posX,
                    onValueChange = { onElementChanged(element.copy(posX = it)) },
                    min = 0f,
                    max = 640f,
                    speed = 1.0f
                )
                ImGuiDragFloat(
                    label = "Pos Y",
                    value = element.posY,
                    onValueChange = { onElementChanged(element.copy(posY = it)) },
                    min = 0f,
                    max = 480f,
                    speed = 1.0f
                )

                Spacer(modifier = Modifier.height(2.dp))

                ImGuiDragFloat(
                    label = "Frame W",
                    value = element.textSizeX,
                    onValueChange = { onElementChanged(element.copy(textSizeX = it)) },
                    min = 10f,
                    max = 640f,
                    speed = 1.0f
                )
                ImGuiDragFloat(
                    label = "Frame H",
                    value = element.textSizeY,
                    onValueChange = { onElementChanged(element.copy(textSizeY = it)) },
                    min = 10f,
                    max = 480f,
                    speed = 1.0f
                )

                // Quick Frame Size Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "50x50" to (50f to 50f),
                        "65x65" to (65f to 65f),
                        "90x90" to (90f to 90f),
                        "120x80" to (120f to 80f)
                    ).forEach { (label, dims) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(ImGuiButton)
                                .clickable {
                                    onElementChanged(element.copy(textSizeX = dims.first, textSizeY = dims.second))
                                }
                                .padding(vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = ImGuiText, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Section: 3D Camera & Orientation
        ImGuiCollapsingHeader(label = "3D Camera Orientation & Zoom") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiDragFloat(
                    label = "Rot X",
                    value = element.modelRotX,
                    onValueChange = { onElementChanged(element.copy(modelRotX = it)) },
                    min = -360f,
                    max = 360f,
                    speed = 1f
                )
                ImGuiDragFloat(
                    label = "Rot Y",
                    value = element.modelRotY,
                    onValueChange = { onElementChanged(element.copy(modelRotY = it)) },
                    min = -360f,
                    max = 360f,
                    speed = 1f
                )
                ImGuiDragFloat(
                    label = "Rot Z",
                    value = element.modelRotZ,
                    onValueChange = { onElementChanged(element.copy(modelRotZ = it)) },
                    min = -360f,
                    max = 360f,
                    speed = 1f
                )
                ImGuiDragFloat(
                    label = "Zoom",
                    value = element.modelZoom,
                    onValueChange = { onElementChanged(element.copy(modelZoom = it)) },
                    min = 0.1f,
                    max = 5.0f,
                    speed = 0.05f,
                    format = "%.2f"
                )
            }
        }

        // Section: Frame Box & Background
        ImGuiCollapsingHeader(label = "Frame Background (UseBox)") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ImGuiCheckbox(
                    label = "Frame Box Background",
                    checked = element.useBox,
                    onCheckedChange = { onElementChanged(element.copy(useBox = it)) }
                )

                if (element.useBox) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BoxColor: ${SampColorUtils.formatToSampPawnHex(element.boxColor)}",
                            color = ImGuiGoldAccent,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SampColorUtils.sampHexToComposeColor(element.boxColor))
                                .border(1.dp, ImGuiBorder, RoundedCornerShape(2.dp))
                        )
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SampColorUtils.gtaColors.takeLast(6).forEach { preset ->
                            val c = SampColorUtils.sampHexToComposeColor(preset.hex)
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(c)
                                    .border(
                                        if (element.boxColor == preset.hex) 1.5.dp else 1.dp,
                                        if (element.boxColor == preset.hex) ImGuiGoldAccent else ImGuiBorder,
                                        RoundedCornerShape(2.dp)
                                    )
                                    .clickable { onElementChanged(element.copy(boxColor = preset.hex)) }
                            )
                        }
                    }
                }
            }
        }

        // Section: Interactive
        ImGuiCollapsingHeader(label = "Interactive Properties") {
            ImGuiCheckbox(
                label = "Selectable (Clickable 3D Item)",
                checked = element.selectable,
                onCheckedChange = { onElementChanged(element.copy(selectable = it)) }
            )
        }
    }
}
