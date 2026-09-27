package com.example.util

import androidx.compose.ui.graphics.Color

object SampColorUtils {

    /**
     * Converts SA-MP RGBA hex format (0xRRGGBBAA) to Jetpack Compose Color (ARGB).
     */
    fun sampHexToComposeColor(sampHex: Long): Color {
        val r = ((sampHex ushr 24) and 0xFF).toInt()
        val g = ((sampHex ushr 16) and 0xFF).toInt()
        val b = ((sampHex ushr 8) and 0xFF).toInt()
        val a = (sampHex and 0xFF).toInt()
        return Color(red = r, green = g, blue = b, alpha = a)
    }

    /**
     * Converts Jetpack Compose Color (ARGB) to SA-MP RGBA Long (0xRRGGBBAA).
     */
    fun composeColorToSampHex(color: Color): Long {
        val r = (color.red * 255).toInt().coerceIn(0, 255).toLong()
        val g = (color.green * 255).toInt().coerceIn(0, 255).toLong()
        val b = (color.blue * 255).toInt().coerceIn(0, 255).toLong()
        val a = (color.alpha * 255).toInt().coerceIn(0, 255).toLong()
        return (r shl 24) or (g shl 16) or (b shl 8) or a
    }

    /**
     * Formats Long to SA-MP Pawn hex string: "0xRRGGBBAA"
     */
    fun formatToSampPawnHex(sampHex: Long): String {
        return "0x%08X".format(sampHex)
    }

    /**
     * Quick GTA SA in-game color palette
     */
    val gtaColors = listOf(
        GtaColorPreset("White (~w~)", 0xFFFFFFFFL),
        GtaColorPreset("Red (~r~)", 0xEE4444FFL),
        GtaColorPreset("Green (~g~)", 0x33CC33FFL),
        GtaColorPreset("Blue (~b~)", 0x3388EEFFL),
        GtaColorPreset("Yellow (~y~)", 0xFFCC00FFL),
        GtaColorPreset("Purple (~p~)", 0x9933CCFFL),
        GtaColorPreset("Black (~l~)", 0x111111FFL),
        GtaColorPreset("Grey", 0x888888FFL),
        GtaColorPreset("Orange", 0xFF6600FFL),
        GtaColorPreset("Cyan", 0x00CCCCFFL),
        GtaColorPreset("Dark Box (50%)", 0x00000080L),
        GtaColorPreset("Dark Box (75%)", 0x000000BFL),
        GtaColorPreset("Transparent Box", 0x1A2234B3L)
    )

    data class GtaColorPreset(val name: String, val hex: Long)
}
