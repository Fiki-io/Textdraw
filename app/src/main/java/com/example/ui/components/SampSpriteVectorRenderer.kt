package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-fidelity Vector Renderers for standard SA-MP / GTA SA Sprites.
 * Renders actual GTA Radar Blips, Weapons, Crosshairs, Diamonds, Badges, etc.
 * directly with crisp vector graphics at any scale!
 */
object SampSpriteVectorRenderer {

    fun drawSprite(
        scope: DrawScope,
        spriteTag: String,
        left: Float,
        top: Float,
        width: Float,
        height: Float,
        tint: Color
    ) {
        val tag = spriteTag.lowercase().trim()
        val cx = left + width / 2f
        val cy = top + height / 2f
        val radius = minOf(width, height) / 2f

        with(scope) {
            when {
                // 0. Solid Texture / Dot / Pixel (LD_BUM:blkdot, LD_SPAC:white, LD_OTB:blkdot, etc.)
                tag.contains("blkdot") || tag.contains("white") || tag.contains("spac") || tag.contains("dot") || tag.contains("solid") -> {
                    drawRect(color = tint, topLeft = Offset(left, top), size = Size(width, height))
                }

                // 1. Radar Disc / Circle
                tag.contains("radardisc") || tag.contains("disc") -> {
                    drawCircle(color = tint.copy(alpha = 0.85f), radius = radius, center = Offset(cx, cy))
                    drawCircle(color = Color.White.copy(alpha = 0.5f), radius = radius, center = Offset(cx, cy), style = Stroke(width = 2f))
                }

                // 2. Radar Center / Crosshair / Sight
                tag.contains("centre") || tag.contains("crosshair") || tag.contains("sight") || tag.contains("sitem16") || tag.contains("siterocket") -> {
                    drawCircle(color = tint, radius = radius * 0.75f, center = Offset(cx, cy), style = Stroke(width = 2f))
                    drawLine(color = tint, start = Offset(cx - radius, cy), end = Offset(cx + radius, cy), strokeWidth = 2f)
                    drawLine(color = tint, start = Offset(cx, cy - radius), end = Offset(cx, cy + radius), strokeWidth = 2f)
                    drawCircle(color = tint, radius = radius * 0.2f, center = Offset(cx, cy))
                }

                // 3. Fist / Weapon / Attack / Knuckle
                tag.contains("fist") || tag.contains("attack") -> {
                    val fW = width * 0.7f
                    val fH = height * 0.55f
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(cx - fW / 2f, cy - fH / 2f),
                        size = Size(fW, fH),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    val knuckleRadius = fW / 10f
                    for (i in -1..2) {
                        drawCircle(
                            color = tint,
                            radius = knuckleRadius,
                            center = Offset(cx + i * (fW / 3.5f) - (fW / 6f), cy - fH / 2f)
                        )
                    }
                }

                // 4. Radar North / Arrow / Direction
                tag.contains("north") || tag.contains("arrow") || tag.contains("waypoint") -> {
                    val path = Path().apply {
                        moveTo(cx, top)
                        lineTo(left + width * 0.75f, top + height)
                        lineTo(cx, top + height * 0.75f)
                        lineTo(left + width * 0.25f, top + height)
                        close()
                    }
                    drawPath(path = path, color = if (tag.contains("north")) Color(0xFFEF4444) else tint, style = Fill)
                    drawPath(path = path, color = Color.White, style = Stroke(width = 1.5f))
                }

                // 5. Radar Cash / Dollar / Money
                tag.contains("cash") || tag.contains("dollar") || tag.contains("money") -> {
                    drawCircle(color = Color(0xFF22C55E), radius = radius * 0.85f, center = Offset(cx, cy))
                    drawCircle(color = Color(0xFF15803D), radius = radius * 0.85f, center = Offset(cx, cy), style = Stroke(width = 2f))
                    drawLine(color = Color.White, start = Offset(cx, cy - radius * 0.55f), end = Offset(cx, cy + radius * 0.55f), strokeWidth = 3f)
                }

                // 6. Guns / Weapons (Ammunation, Emmet gun)
                tag.contains("ammu") || tag.contains("gun") || tag.contains("weapon") -> {
                    val p = Path().apply {
                        moveTo(left + width * 0.15f, top + height * 0.35f)
                        lineTo(left + width * 0.85f, top + height * 0.35f)
                        lineTo(left + width * 0.85f, top + height * 0.55f)
                        lineTo(left + width * 0.5f, top + height * 0.55f)
                        lineTo(left + width * 0.35f, top + height * 0.85f)
                        lineTo(left + width * 0.15f, top + height * 0.8f)
                        close()
                    }
                    drawPath(path = p, color = tint)
                    drawPath(path = p, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 1.5f))
                }

                // 7. Police / Badge / Star
                tag.contains("police") || tag.contains("star") || tag.contains("badge") -> {
                    val starPath = Path()
                    val points = 5
                    val outerR = radius * 0.85f
                    val innerR = outerR * 0.45f
                    for (i in 0 until points * 2) {
                        val r = if (i % 2 == 0) outerR else innerR
                        val angle = (i * Math.PI / points) - (Math.PI / 2.0)
                        val px = cx + (r * cos(angle)).toFloat()
                        val py = cy + (r * sin(angle)).toFloat()
                        if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
                    }
                    starPath.close()
                    drawPath(path = starPath, color = Color(0xFF3B82F6), style = Fill)
                    drawPath(path = starPath, color = Color.White, style = Stroke(width = 1.5f))
                }

                // 8. Fire / Flame
                tag.contains("fire") -> {
                    val flamePath = Path().apply {
                        moveTo(cx, top + height * 0.1f)
                        cubicTo(left + width * 0.9f, top + height * 0.4f, left + width * 0.85f, top + height * 0.9f, cx, top + height * 0.95f)
                        cubicTo(left + width * 0.15f, top + height * 0.9f, left + width * 0.1f, top + height * 0.4f, cx, top + height * 0.1f)
                        close()
                    }
                    drawPath(path = flamePath, color = Color(0xFFF97316), style = Fill)
                }

                // 9. Hospital / Medic / Health / Cross
                tag.contains("hostpital") || tag.contains("hospital") || tag.contains("medic") || tag.contains("health") -> {
                    val crossW = width * 0.25f
                    val crossL = width * 0.75f
                    drawRect(color = Color(0xFFEF4444), topLeft = Offset(cx - crossW / 2f, cy - crossL / 2f), size = Size(crossW, crossL))
                    drawRect(color = Color(0xFFEF4444), topLeft = Offset(cx - crossL / 2f, cy - crossW / 2f), size = Size(crossL, crossW))
                }

                // 10. Food / Burger / Pizza
                tag.contains("burger") || tag.contains("food") || tag.contains("diner") || tag.contains("pizza") || tag.contains("chicken") -> {
                    drawArc(
                        color = Color(0xFFF59E0B),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(cx - radius * 0.8f, cy - radius * 0.8f),
                        size = Size(radius * 1.6f, radius * 1.2f)
                    )
                    drawRoundRect(
                        color = Color(0xFFB45309),
                        topLeft = Offset(cx - radius * 0.75f, cy + radius * 0.1f),
                        size = Size(radius * 1.5f, radius * 0.35f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }

                // 11. Property Green / Red (Buy/Sell House)
                tag.contains("propertyg") -> {
                    val house = Path().apply {
                        moveTo(cx, top + height * 0.15f)
                        lineTo(left + width * 0.85f, top + height * 0.45f)
                        lineTo(left + width * 0.75f, top + height * 0.45f)
                        lineTo(left + width * 0.75f, top + height * 0.85f)
                        lineTo(left + width * 0.25f, top + height * 0.85f)
                        lineTo(left + width * 0.25f, top + height * 0.45f)
                        lineTo(left + width * 0.15f, top + height * 0.45f)
                        close()
                    }
                    drawPath(path = house, color = Color(0xFF22C55E), style = Fill)
                    drawPath(path = house, color = Color.White, style = Stroke(width = 1.5f))
                }
                tag.contains("propertyr") -> {
                    val house = Path().apply {
                        moveTo(cx, top + height * 0.15f)
                        lineTo(left + width * 0.85f, top + height * 0.45f)
                        lineTo(left + width * 0.75f, top + height * 0.45f)
                        lineTo(left + width * 0.75f, top + height * 0.85f)
                        lineTo(left + width * 0.25f, top + height * 0.85f)
                        lineTo(left + width * 0.25f, top + height * 0.45f)
                        lineTo(left + width * 0.15f, top + height * 0.45f)
                        close()
                    }
                    drawPath(path = house, color = Color(0xFFEF4444), style = Fill)
                    drawPath(path = house, color = Color.White, style = Stroke(width = 1.5f))
                }

                // 12. Gang Areas (B: Ballas/Purple, G: Grove/Green, Y: Vagos/Yellow)
                tag.contains("gangb") -> {
                    drawRect(color = Color(0xFF9333EA), topLeft = Offset(left, top), size = Size(width, height))
                    drawRect(color = Color.White, topLeft = Offset(left, top), size = Size(width, height), style = Stroke(width = 2f))
                }
                tag.contains("gangg") -> {
                    drawRect(color = Color(0xFF16A34A), topLeft = Offset(left, top), size = Size(width, height))
                    drawRect(color = Color.White, topLeft = Offset(left, top), size = Size(width, height), style = Stroke(width = 2f))
                }
                tag.contains("gangy") -> {
                    drawRect(color = Color(0xFFCA8A04), topLeft = Offset(left, top), size = Size(width, height))
                    drawRect(color = Color.White, topLeft = Offset(left, top), size = Size(width, height), style = Stroke(width = 2f))
                }

                // 13. Vehicles / Race / Mod Garage / Spray / Truck
                tag.contains("race") || tag.contains("modgarage") || tag.contains("spray") || tag.contains("truck") -> {
                    val carPath = Path().apply {
                        moveTo(left + width * 0.2f, top + height * 0.6f)
                        lineTo(left + width * 0.35f, top + height * 0.35f)
                        lineTo(left + width * 0.65f, top + height * 0.35f)
                        lineTo(left + width * 0.8f, top + height * 0.6f)
                        lineTo(left + width * 0.9f, top + height * 0.6f)
                        lineTo(left + width * 0.9f, top + height * 0.8f)
                        lineTo(left + width * 0.1f, top + height * 0.8f)
                        lineTo(left + width * 0.1f, top + height * 0.6f)
                        close()
                    }
                    drawPath(path = carPath, color = tint, style = Fill)
                    drawCircle(color = Color.Black, radius = radius * 0.2f, center = Offset(left + width * 0.3f, top + height * 0.8f))
                    drawCircle(color = Color.Black, radius = radius * 0.2f, center = Offset(left + width * 0.7f, top + height * 0.8f))
                }

                // 14. LD_BEAT Navigation & UI Shapes
                tag.contains("up") -> {
                    val p = Path().apply {
                        moveTo(cx, top + height * 0.1f)
                        lineTo(left + width * 0.9f, top + height * 0.85f)
                        lineTo(left + width * 0.1f, top + height * 0.85f)
                        close()
                    }
                    drawPath(path = p, color = tint)
                }
                tag.contains("down") -> {
                    val p = Path().apply {
                        moveTo(cx, top + height * 0.9f)
                        lineTo(left + width * 0.9f, top + height * 0.15f)
                        lineTo(left + width * 0.1f, top + height * 0.15f)
                        close()
                    }
                    drawPath(path = p, color = tint)
                }
                tag.contains("left") -> {
                    val p = Path().apply {
                        moveTo(left + width * 0.1f, cy)
                        lineTo(left + width * 0.85f, top + height * 0.1f)
                        lineTo(left + width * 0.85f, top + height * 0.9f)
                        close()
                    }
                    drawPath(path = p, color = tint)
                }
                tag.contains("right") -> {
                    val p = Path().apply {
                        moveTo(left + width * 0.9f, cy)
                        lineTo(left + width * 0.15f, top + height * 0.1f)
                        lineTo(left + width * 0.15f, top + height * 0.9f)
                        close()
                    }
                    drawPath(path = p, color = tint)
                }
                tag.contains("circle") || tag.contains("cring") -> {
                    drawCircle(color = tint, radius = radius * 0.8f, center = Offset(cx, cy), style = Stroke(width = 3f))
                }
                tag.contains("square") -> {
                    drawRoundRect(color = tint, topLeft = Offset(left + width * 0.1f, top + height * 0.1f), size = Size(width * 0.8f, height * 0.8f), cornerRadius = CornerRadius(4f, 4f))
                }
                tag.contains("triang") -> {
                    val p = Path().apply {
                        moveTo(cx, top + height * 0.15f)
                        lineTo(left + width * 0.85f, top + height * 0.85f)
                        lineTo(left + width * 0.15f, top + height * 0.85f)
                        close()
                    }
                    drawPath(path = p, color = tint, style = Stroke(width = 3f))
                }
                tag.contains("cross") -> {
                    drawLine(color = tint, start = Offset(left + width * 0.2f, top + height * 0.2f), end = Offset(left + width * 0.8f, top + height * 0.8f), strokeWidth = 3f)
                    drawLine(color = tint, start = Offset(left + width * 0.8f, top + height * 0.2f), end = Offset(left + width * 0.2f, top + height * 0.8f), strokeWidth = 3f)
                }
                tag.contains("chit") || tag.contains("diamond") -> {
                    val diamond = Path().apply {
                        moveTo(cx, top)
                        lineTo(left + width, cy)
                        lineTo(cx, top + height)
                        lineTo(left, cy)
                        close()
                    }
                    drawPath(path = diamond, color = tint, style = Fill)
                    drawPath(path = diamond, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = 1.5f))
                }

                // 15. Cards (LD_CARD)
                tag.contains("ld_card") || tag.contains("cd") -> {
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(left + width * 0.1f, top + height * 0.05f),
                        size = Size(width * 0.8f, height * 0.9f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = Color.Black,
                        topLeft = Offset(left + width * 0.1f, top + height * 0.05f),
                        size = Size(width * 0.8f, height * 0.9f),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = 1.5f)
                    )
                    val cardSuitColor = if (tag.contains("h") || tag.contains("d")) Color(0xFFEF4444) else Color(0xFF1E293B)
                    drawCircle(color = cardSuitColor, radius = radius * 0.3f, center = Offset(cx, cy))
                }

                // 16. Chat Thumbs & Dpad (LD_CHAT)
                tag.contains("thumbup") -> {
                    drawCircle(color = Color(0xFF22C55E), radius = radius * 0.8f, center = Offset(cx, cy))
                    drawLine(color = Color.White, start = Offset(cx - radius * 0.4f, cy), end = Offset(cx - radius * 0.1f, cy + radius * 0.35f), strokeWidth = 3f)
                    drawLine(color = Color.White, start = Offset(cx - radius * 0.1f, cy + radius * 0.35f), end = Offset(cx + radius * 0.45f, cy - radius * 0.35f), strokeWidth = 3f)
                }
                tag.contains("thumbdn") -> {
                    drawCircle(color = Color(0xFFEF4444), radius = radius * 0.8f, center = Offset(cx, cy))
                    drawLine(color = Color.White, start = Offset(cx - radius * 0.3f, cy - radius * 0.3f), end = Offset(cx + radius * 0.3f, cy + radius * 0.3f), strokeWidth = 3f)
                    drawLine(color = Color.White, start = Offset(cx + radius * 0.3f, cy - radius * 0.3f), end = Offset(cx - radius * 0.3f, cy + radius * 0.3f), strokeWidth = 3f)
                }

                // 17. Character Radars (CJ, Ryder, Big Smoke, Sweet, Catalina Pink, etc.)
                tag.contains("cj") || tag.contains("ryder") || tag.contains("bigsmoke") || tag.contains("sweet") || tag.contains("woozie") || tag.contains("zero") || tag.contains("toreno") || tag.contains("catalina") -> {
                    val charColor = when {
                        tag.contains("catalina") -> Color(0xFFEC4899)
                        tag.contains("sweet") || tag.contains("cj") -> Color(0xFF22C55E)
                        tag.contains("ryder") || tag.contains("bigsmoke") -> Color(0xFFEAB308)
                        else -> Color(0xFF3B82F6)
                    }
                    drawCircle(color = charColor, radius = radius * 0.85f, center = Offset(cx, cy))
                    drawCircle(color = Color.White, radius = radius * 0.85f, center = Offset(cx, cy), style = Stroke(width = 2f))
                    // Head silhouette
                    drawCircle(color = Color.White, radius = radius * 0.35f, center = Offset(cx, cy - radius * 0.15f))
                    // Shoulders
                    drawArc(
                        color = Color.White,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(cx - radius * 0.55f, cy + radius * 0.05f),
                        size = Size(radius * 1.1f, radius * 0.8f)
                    )
                }

                // 18. Default White Texture (LD_SPAC:white) or Solid Box
                tag.contains("white") || tag.contains("spac") -> {
                    drawRect(color = tint, topLeft = Offset(left, top), size = Size(width, height))
                }

                // Default: Distinctive Game Icon with Tag Initials
                else -> {
                    val p = Path().apply {
                        moveTo(left + width * 0.1f, top + height * 0.1f)
                        lineTo(left + width * 0.9f, top + height * 0.1f)
                        lineTo(left + width * 0.85f, top + height * 0.7f)
                        lineTo(cx, top + height * 0.95f)
                        lineTo(left + width * 0.15f, top + height * 0.7f)
                        close()
                    }
                    drawPath(path = p, color = tint.copy(alpha = 0.8f), style = Fill)
                    drawPath(path = p, color = Color.White.copy(alpha = 0.6f), style = Stroke(width = 1.5f))

                    // Inner geometric emblem
                    drawCircle(color = Color.White, radius = radius * 0.28f, center = Offset(cx, cy))
                }
            }
        }
    }
}
