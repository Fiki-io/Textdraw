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
 * Parses SA-MP color tags (~r~, ~g~, ~b~, ~y~, ~w~, ~s~, ~h~, ~p~) into Compose AnnotatedString
 */
private fun parseSampTags(rawText: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var currentColor = defaultColor
        var i = 0
        while (i < rawText.length) {
            if (rawText[i] == '~' && i + 2 < rawText.length && rawText[i + 2] == '~') {
                val tag = rawText.substring(i, i + 3).lowercase()
                currentColor = when (tag) {
                    "~r~" -> SampRed
                    "~g~" -> SampGreen
                    "~b~" -> SampBlue
                    "~y~" -> SampYellow
                    "~w~" -> SampWhite
                    "~s~" -> SampGray
                    "~h~" -> Color(0xFFE2E8F0)
                    "~p~" -> SampPurple
                    else -> currentColor
                }
                i += 3
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
    // For Font 4 (Sprites), Font 5 (3D Models), or Blank Boxes
    if (element.font == 4 || element.font == 5 || (element.useBox && element.text.trim().isEmpty())) {
        val rawW = element.textSizeX
        val rawH = element.textSizeY

        val finalX = if (rawW < 0) element.posX + rawW else element.posX
        val finalY = if (rawH < 0) element.posY + rawH else element.posY
        val finalW = kotlin.math.abs(rawW)
        val finalH = kotlin.math.abs(rawH)

        return ElementPixelBounds(
            left = finalX * scale,
            top = finalY * scale,
            width = (if (finalW > 0f) finalW else 16f) * scale,
            height = (if (finalH > 0f) finalH else 16f) * scale
        )
    }

    val pixelX = element.posX * scale
    val pixelY = element.posY * scale

    // Authentic SA-MP font scaling (1 SA-MP letterSizeY unit = ~9.6 virtual pixels on 480p canvas)
    val targetPx = element.letterSizeY * 9.6f * scale
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

    if (element.useBox) {
        val rawBoxW = element.textSizeX * scale
        val rawBoxH = element.textSizeY * scale
        val boxW = max(textW, kotlin.math.abs(rawBoxW))
        val boxH = max(textH, kotlin.math.abs(rawBoxH))
        val boxLeft = when (element.alignment) {
            2 -> pixelX - (boxW / 2f)
            3 -> pixelX - boxW
            else -> pixelX
        }
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

    val composeColor = SampColorUtils.sampHexToComposeColor(element.color)
    val composeBoxColor = SampColorUtils.sampHexToComposeColor(element.boxColor)

    val bounds = calculateElementBounds(element, textMeasurer, scale, density)

    // 1. Box
    if (element.useBox && element.font != 4) {
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
            val targetPx = element.letterSizeY * 9.6f * scale
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

            // Outline / Shadow
            if (element.outline > 0) {
                val outPx = element.outline * scale
                val offsets = listOf(
                    Offset(-outPx, 0f), Offset(outPx, 0f), Offset(0f, -outPx), Offset(0f, outPx)
                )
                val outAnnotated = parseSampTags(element.text.ifEmpty { " " }, Color.Black)
                for (off in offsets) {
                    val outResult = textMeasurer.measure(text = outAnnotated, style = textStyle)
                    drawText(textLayoutResult = outResult, topLeft = Offset(textDrawX + off.x, textDrawY + off.y))
                }
            } else if (element.shadow > 0) {
                val shPx = element.shadow * scale
                val shadowAnnotated = parseSampTags(element.text.ifEmpty { " " }, Color.Black.copy(alpha = 0.8f))
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
