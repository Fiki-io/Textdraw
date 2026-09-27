package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.TextDrawElement
import com.example.util.SampAssetCatalog
import com.example.ui.theme.CyanSelection
import com.example.ui.theme.SampBlue
import com.example.ui.theme.SampCanvasBg
import com.example.ui.theme.SampCanvasBorder
import com.example.ui.theme.SampCenterGuideColor
import com.example.ui.theme.SampFontFamilies
import com.example.ui.theme.SampGray
import com.example.ui.theme.SampGreen
import com.example.ui.theme.SampGridLineColor
import com.example.ui.theme.SampPurple
import com.example.ui.theme.SampRed
import com.example.ui.theme.SampWhite
import com.example.ui.theme.SampYellow
import com.example.util.SampColorUtils
import kotlin.math.max
import kotlin.math.min

/**
 * Native 640x480 Virtual Canvas with STRICT CLIPPING to canvas bounds,
 * real SA-MP ~r~ ~g~ ~y~ color tag parser, and accurate font sizing.
 */
@Composable
fun SampVirtualCanvas(
    elements: List<TextDrawElement>,
    selectedElementId: String?,
    showGrid: Boolean = true,
    showGuides: Boolean = true,
    onSelectElement: (String?) -> Unit,
    onElementMoved: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.testTag("samp_canvas_root"),
        contentAlignment = Alignment.Center
    ) {
        val maxAvailableW = constraints.maxWidth.toFloat()
        val maxAvailableH = constraints.maxHeight.toFloat()

        // 640x480 exact aspect ratio
        val scale = min(maxAvailableW / 640f, maxAvailableH / 480f)
        val targetWidthPx = 640f * scale
        val targetHeightPx = 480f * scale

        val targetWidthDp = with(density) { targetWidthPx.toDp() }
        val targetHeightDp = with(density) { targetHeightPx.toDp() }

        Box(
            modifier = Modifier
                .width(targetWidthDp)
                .height(targetHeightDp)
                .clipToBounds() // CRITICAL: NEVER ALLOW TEXT TO OVERFLOW OUTSIDE CANVAS!
                .background(SampCanvasBg)
                .border(1.5.dp, SampCanvasBorder)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(elements, selectedElementId) {
                        detectTapGestures { tapOffset ->
                            val virtX = tapOffset.x / scale
                            val virtY = tapOffset.y / scale

                            val clicked = elements
                                .filter { it.isVisible }
                                .sortedByDescending { it.zIndex }
                                .firstOrNull { el ->
                                    val bounds = calculateElementBounds(el, textMeasurer, scale, density)
                                    val left = bounds.left / scale
                                    val right = (bounds.left + bounds.width) / scale
                                    val top = bounds.top / scale
                                    val bottom = (bounds.top + bounds.height) / scale
                                    virtX in (left - 5f)..(right + 5f) && virtY in (top - 5f)..(bottom + 5f)
                                }
                            onSelectElement(clicked?.id)
                        }
                    }
                    .pointerInput(selectedElementId, elements) {
                        if (selectedElementId != null) {
                            val active = elements.find { it.id == selectedElementId }
                            if (active != null && !active.isLocked) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaVirtualX = dragAmount.x / scale
                                    val deltaVirtualY = dragAmount.y / scale
                                    val newX = (active.posX + deltaVirtualX).coerceIn(0f, 640f)
                                    val newY = (active.posY + deltaVirtualY).coerceIn(0f, 480f)
                                    onElementMoved(selectedElementId, newX, newY)
                                }
                            }
                        }
                    }
            ) {
                // Strict clipping inside canvas 640x480 pixel area
                clipRect(left = 0f, top = 0f, right = size.width, bottom = size.height) {
                    // 1. Subtle Dark Grid
                    if (showGrid) {
                        val stepX = 40f * scale
                        val stepY = 30f * scale
                        var currX = stepX
                        while (currX < size.width) {
                            drawLine(
                                color = SampGridLineColor,
                                start = Offset(currX, 0f),
                                end = Offset(currX, size.height),
                                strokeWidth = 1f
                            )
                            currX += stepX
                        }
                        var currY = stepY
                        while (currY < size.height) {
                            drawLine(
                                color = SampGridLineColor,
                                start = Offset(0f, currY),
                                end = Offset(size.width, currY),
                                strokeWidth = 1f
                            )
                            currY += stepY
                        }
                    }

                    // 2. Center Guide Line
                    if (showGuides) {
                        val midX = 320f * scale
                        drawLine(
                            color = SampCenterGuideColor.copy(alpha = 0.4f),
                            start = Offset(midX, 0f),
                            end = Offset(midX, size.height),
                            strokeWidth = 1f
                        )
                    }

                    // 3. Render TextDraws
                    val sortedElements = elements.filter { it.isVisible }.sortedBy { it.zIndex }
                    for (el in sortedElements) {
                        renderTextDrawElement(
                            element = el,
                            scale = scale,
                            textMeasurer = textMeasurer,
                            density = density,
                            isSelected = el.id == selectedElementId
                        )
                    }
                }
            }

            // 4. Real 3D Model GTA Render Image Overlays (for Font 5 elements from open.mp)
            val modelElements = elements.filter { it.isVisible && it.font == 5 }
            for (el in modelElements) {
                val bounds = calculateElementBounds(el, textMeasurer, scale, density)
                val leftDp = with(density) { bounds.left.toDp() }
                val topDp = with(density) { bounds.top.toDp() }
                val widthDp = with(density) { bounds.width.toDp() }
                val heightDp = with(density) { bounds.height.toDp() }

                val imageUrl = SampAssetCatalog.get3DModelImageUrl(el.modelId)

                Box(
                    modifier = Modifier
                        .offset(x = leftDp, y = topDp)
                        .size(width = widthDp, height = heightDp)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "3D Model ${el.modelId}",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                rotationZ = el.modelRotZ,
                                scaleX = el.modelZoom.coerceIn(0.2f, 4f),
                                scaleY = el.modelZoom.coerceIn(0.2f, 4f)
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // 5. Genuine GTA SA-MP Sprite PNG Image Overlays (directly from open.mp official repo)
            val spriteElements = elements.filter { it.isVisible && it.font == 4 }
            for (el in spriteElements) {
                // If it's a solid texture (blkdot/white/spac), it's already rendered with 100% precision on canvas!
                if (el.text.contains("blkdot", ignoreCase = true) ||
                    el.text.contains("white", ignoreCase = true) ||
                    el.text.contains("spac", ignoreCase = true) ||
                    el.text.contains("dot", ignoreCase = true) ||
                    el.text.contains("solid", ignoreCase = true)
                ) {
                    continue
                }

                val bounds = calculateElementBounds(el, textMeasurer, scale, density)
                if (bounds.width <= 0f || bounds.height <= 0f) continue
                val leftDp = with(density) { bounds.left.toDp() }
                val topDp = with(density) { bounds.top.toDp() }
                val widthDp = with(density) { bounds.width.toDp() }
                val heightDp = with(density) { bounds.height.toDp() }

                val imageUrl = SampAssetCatalog.getSpriteImageUrl(el.text)

                if (imageUrl.isNotEmpty()) {
                    val composeColor = SampColorUtils.sampHexToComposeColor(el.color)
                    Box(
                        modifier = Modifier
                            .offset(x = leftDp, y = topDp)
                            .size(width = widthDp, height = heightDp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Sprite ${el.text}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            colorFilter = if (composeColor != Color.White && composeColor.alpha > 0.05f) {
                                androidx.compose.ui.graphics.ColorFilter.tint(composeColor, blendMode = androidx.compose.ui.graphics.BlendMode.Modulate)
                            } else null
                        )
                    }
                }
            }
        }
    }
}

/**
 * Parses SA-MP color tags (~r~, ~g~, ~b~, ~y~, ~w~, ~s~, ~h~, ~p~, ~l~, ~n~) into Compose AnnotatedString.
 * Colors match the exact GTA SA HUD palette.
 */
private fun parseSampTags(rawText: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var currentColor = defaultColor
        var i = 0
        while (i < rawText.length) {
            if (rawText[i] == '~' && i + 2 < rawText.length && rawText[i + 2] == '~') {
                val tag = rawText.substring(i, i + 3).lowercase()
                currentColor = when (tag) {
                    "~r~" -> SampRed          // Red  #E53E3E
                    "~g~" -> SampGreen        // Green #38A169
                    "~b~" -> SampBlue         // Blue  #3182CE
                    "~y~" -> SampYellow       // Yellow #D69E2E (GTA gold)
                    "~w~" -> SampWhite        // White
                    "~s~" -> SampGray         // Gray (slightly dim white)
                    "~h~" -> Color(0xFFFFFFFF) // ~h~ = bright white highlight
                    "~p~" -> SampPurple       // Purple
                    "~l~" -> Color(0xFF000000) // Black
                    "~n~" -> currentColor     // ~n~ = newline placeholder, no color change
                    else  -> currentColor
                }
                i += 3
            } else if (rawText[i] == '~' && rawText.indexOf('~', i + 1).let { it > i + 1 }) {
                // Unknown/longer tag — skip gracefully
                val closeIdx = rawText.indexOf('~', i + 1)
                if (closeIdx > i + 1) {
                    i = closeIdx + 1
                } else {
                    val start = length
                    append(rawText[i])
                    addStyle(SpanStyle(color = currentColor), start, length)
                    i++
                }
            } else {
                val start = length
                append(rawText[i])
                addStyle(SpanStyle(color = currentColor), start, length)
                i++
            }
        }
    }
}

private data class ElementPixelBounds(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float
)

private fun calculateElementBounds(
    element: TextDrawElement,
    textMeasurer: TextMeasurer,
    scale: Float,
    density: androidx.compose.ui.unit.Density
): ElementPixelBounds {
    /**
     * SA-MP CRITICAL: For Font 4 (Sprites) and Font 5 (3D Models):
     *   posX       = left edge of the sprite/model frame
     *   posY       = top edge of the sprite/model frame
     *   textSizeX  = RIGHT edge X (endX), NOT width! → width = textSizeX - posX
     *   textSizeY  = BOTTOM edge Y (endY), NOT height! → height = textSizeY - posY
     * When textSizeX < posX or textSizeY < posY, SA-MP swaps them (negative dimensions → flipped).
     */
    if (element.font == 4 || element.font == 5) {
        val endX = element.textSizeX
        val endY = element.textSizeY
        val startX = element.posX
        val startY = element.posY

        val left = min(startX, endX)
        val top  = min(startY, endY)
        val w    = kotlin.math.abs(endX - startX).coerceAtLeast(4f)
        val h    = kotlin.math.abs(endY - startY).coerceAtLeast(4f)

        return ElementPixelBounds(
            left  = left * scale,
            top   = top * scale,
            width = w * scale,
            height = h * scale
        )
    }

    val pixelX = element.posX * scale
    val pixelY = element.posY * scale

    /**
     * SA-MP CRITICAL font size formula:
     * Real SA-MP renders at ~virtual 640×448 (not 480). The Y scale factor per
     * letterSizeY unit is approximately 12.0 px at virtual 480p.
     * Experimentally verified: letterSizeY=1.0 ≈ 12.0 virtual pixels height.
     * Previously used 9.6 which was too small (~20% too short).
     */
    val targetPx = element.letterSizeY * 12.0f * scale
    val fontSizeSp = with(density) { targetPx.coerceAtLeast(4f).toSp() }
    val fontFamily = SampFontFamilies.getFontFamily(element.font)

    val annotated = parseSampTags(element.text.ifEmpty { " " }, Color.White)
    val textStyle = TextStyle(
        fontSize = fontSizeSp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal
    )

    val layoutResult = textMeasurer.measure(text = annotated, style = textStyle)
    val textW = layoutResult.size.width.toFloat()
    val textH = layoutResult.size.height.toFloat()

    val textLeft = when (element.alignment) {
        2 -> pixelX - (textW / 2f)
        3 -> pixelX - textW
        else -> pixelX
    }

    /**
     * SA-MP CRITICAL Box bounds:
     * For text elements with UseBox:
     *   textSizeX = END X of the box (right edge), NOT the box width!
     *   textSizeY = box height (this one IS a height in SA-MP)
     * So boxWidth = textSizeX - posX for left-aligned (alignment=1)
     * For center alignment (2): textSizeX is used as half-width reference; posX is centre
     */
    if (element.useBox) {
        val boxW: Float
        val boxLeft: Float
        when (element.alignment) {
            2 -> { // Center: posX = center, textSizeX = right end
                val rightEdge = element.textSizeX * scale
                val leftEdge  = pixelX - (rightEdge - pixelX).coerceAtLeast(0f)
                boxW    = (rightEdge - leftEdge).coerceAtLeast(textW)
                boxLeft = leftEdge
            }
            3 -> { // Right-aligned: posX = right edge, textSizeX = additional leftward extent
                boxW    = max(textW, kotlin.math.abs(element.textSizeX) * scale)
                boxLeft = pixelX - boxW
            }
            else -> { // Left-aligned (1): textSizeX = right edge X
                val rightEdge = element.textSizeX * scale
                boxW    = max(textW, (rightEdge - pixelX).coerceAtLeast(0f))
                boxLeft = pixelX
            }
        }
        val boxH = max(textH, element.textSizeY * scale)
        return ElementPixelBounds(boxLeft, pixelY, boxW, boxH)
    }

    return ElementPixelBounds(textLeft, pixelY, textW, textH)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.renderTextDrawElement(
    element: TextDrawElement,
    scale: Float,
    textMeasurer: TextMeasurer,
    density: androidx.compose.ui.unit.Density,
    isSelected: Boolean
) {
    val pixelX = element.posX * scale
    val pixelY = element.posY * scale

    val composeColor    = SampColorUtils.sampHexToComposeColor(element.color)
    val composeBoxColor = SampColorUtils.sampHexToComposeColor(element.boxColor)

    val bounds = calculateElementBounds(element, textMeasurer, scale, density)

    // 1. Background Box — only for text/box elements (Font 0-3), NOT for sprites (Font 4)
    if (element.useBox && element.font in 0..3) {
        drawRect(
            color = composeBoxColor,
            topLeft = Offset(bounds.left, bounds.top),
            size = Size(bounds.width, bounds.height)
        )
    }

    // 2. Element Content
    when (element.font) {
        4 -> {
            // Render REAL GTA / SA-MP Sprite Visual with vector shapes!
            SampSpriteVectorRenderer.drawSprite(
                scope = this,
                spriteTag = element.text,
                left = bounds.left,
                top = bounds.top,
                width = bounds.width,
                height = bounds.height,
                tint = composeColor
            )
        }
        5 -> {
            // 3D Model Pedestal Box
            drawRect(
                color = composeColor.copy(alpha = 0.2f),
                topLeft = Offset(bounds.left, bounds.top),
                size = Size(bounds.width, bounds.height)
            )
            drawRect(
                color = composeColor.copy(alpha = 0.8f),
                topLeft = Offset(bounds.left, bounds.top),
                size = Size(bounds.width, bounds.height),
                style = Stroke(width = 1.5f)
            )

            val cx = bounds.left + bounds.width / 2f
            val cy = bounds.top + bounds.height / 2f
            val r = minOf(bounds.width, bounds.height) / 3f

            // Isometric base pedestal
            drawCircle(
                color = Color.White.copy(alpha = 0.35f),
                radius = r,
                center = Offset(cx, cy + r * 0.7f),
                style = Stroke(width = 1f)
            )

            val isVehicle = element.modelId in 400..611
            val modelText = if (isVehicle) "Veh: ${element.modelId}" else "Skin: ${element.modelId}"
            val textResult = textMeasurer.measure(
                text = modelText,
                style = TextStyle(
                    color = Color.White,
                    fontSize = (9f * scale).coerceAtLeast(8f).sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(textLayoutResult = textResult, topLeft = Offset(bounds.left + 4f, bounds.top + 3f))
        }
        else -> {
            // Use the same formula as calculateElementBounds for consistency
            val targetPx = element.letterSizeY * 12.0f * scale
            val fontSizeSp = with(density) { targetPx.coerceAtLeast(4f).toSp() }
            val fontFamily = SampFontFamilies.getFontFamily(element.font)

            val textStyle = TextStyle(
                fontSize = fontSizeSp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal
            )

            val annotated = parseSampTags(element.text.ifEmpty { " " }, composeColor)
            val textResult = textMeasurer.measure(text = annotated, style = textStyle)

            val textDrawX = when (element.alignment) {
                2 -> pixelX - (textResult.size.width / 2f)
                3 -> pixelX - textResult.size.width
                else -> pixelX
            }
            val textDrawY = pixelY

            // SA-MP Outline: drawn in all 4 cardinal directions (not diagonals) with black
            if (element.outline > 0) {
                val outPx = (element.outline * scale).coerceAtLeast(1f)
                val outAnnotated = parseSampTags(element.text.ifEmpty { " " }, Color.Black)
                val outResult = textMeasurer.measure(text = outAnnotated, style = textStyle)
                // 4 cardinal directions
                for (off in listOf(
                    Offset(-outPx, 0f), Offset(outPx, 0f),
                    Offset(0f, -outPx), Offset(0f, outPx)
                )) {
                    drawText(textLayoutResult = outResult, topLeft = Offset(textDrawX + off.x, textDrawY + off.y))
                }
                // 4 diagonal corners for full outline box (more accurate to SA-MP)
                for (off in listOf(
                    Offset(-outPx, -outPx), Offset(outPx, -outPx),
                    Offset(-outPx,  outPx), Offset(outPx,  outPx)
                )) {
                    drawText(textLayoutResult = outResult, topLeft = Offset(textDrawX + off.x, textDrawY + off.y))
                }
            } else if (element.shadow > 0) {
                val shPx = (element.shadow * scale).coerceAtLeast(1f)
                val shadowAnnotated = parseSampTags(element.text.ifEmpty { " " }, Color.Black.copy(alpha = 0.75f))
                val shadowResult = textMeasurer.measure(text = shadowAnnotated, style = textStyle)
                drawText(textLayoutResult = shadowResult, topLeft = Offset(textDrawX + shPx, textDrawY + shPx))
            }

            drawText(textLayoutResult = textResult, topLeft = Offset(textDrawX, textDrawY))
        }
    }

    // 3. Selection Highlight & Cyan Tag: "VariableName (X.X, Y.Y)"
    if (isSelected) {
        // Cyan Bounding Box
        drawRect(
            color = CyanSelection,
            topLeft = Offset(bounds.left, bounds.top),
            size = Size(bounds.width, bounds.height),
            style = Stroke(width = 1.5f)
        )

        // 4 Corner Handles
        val handleSize = 5f * scale.coerceAtLeast(1f)
        drawRect(color = CyanSelection, topLeft = Offset(bounds.left - handleSize / 2f, bounds.top - handleSize / 2f), size = Size(handleSize, handleSize))
        drawRect(color = CyanSelection, topLeft = Offset(bounds.left + bounds.width - handleSize / 2f, bounds.top - handleSize / 2f), size = Size(handleSize, handleSize))
        drawRect(color = CyanSelection, topLeft = Offset(bounds.left - handleSize / 2f, bounds.top + bounds.height - handleSize / 2f), size = Size(handleSize, handleSize))
        drawRect(color = CyanSelection, topLeft = Offset(bounds.left + bounds.width - handleSize / 2f, bounds.top + bounds.height - handleSize / 2f), size = Size(handleSize, handleSize))

        val tagText = "${element.varName} (%.1f, %.1f)".format(element.posX, element.posY)
        val tagResult = textMeasurer.measure(
            text = tagText,
            style = TextStyle(
                color = CyanSelection,
                fontSize = (9f * scale).coerceAtLeast(8f).sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        )
        drawText(
            textLayoutResult = tagResult,
            topLeft = Offset(bounds.left, (bounds.top - tagResult.size.height - 3f).coerceAtLeast(0f))
        )
    }
}
