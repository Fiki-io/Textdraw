package com.example.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

object SampFontFamilies {
    // Font 0: SA-MP Bank Gothic — uppercase caps, GTA area/vehicle names
    val BankGothic = FontFamily(
        Font(R.font.bebas_neue, FontWeight.Bold)
    )

    // Font 1: SA-MP Standard (Chalet London) — default body text
    val Chalet = FontFamily(
        Font(R.font.chivo, FontWeight.Normal)
    )

    // Font 2: SA-MP Pricedown — GTA title font, money/stats display
    val Pricedown = FontFamily(
        Font(R.font.pricedown, FontWeight.Normal)
    )

    // Font 3: SA-MP Diploma Old English — GTA calligraphy/license font
    val Diploma = FontFamily(
        Font(R.font.diploma, FontWeight.Normal)
    )

    fun getFontFamily(fontIndex: Int): FontFamily {
        return when (fontIndex) {
            0 -> BankGothic   // SA-MP Font 0: Bank Gothic (uppercase)
            1 -> Chalet       // SA-MP Font 1: Standard / Chalet
            2 -> Pricedown    // SA-MP Font 2: Pricedown (GTA title)
            3 -> Diploma      // SA-MP Font 3: Diploma (Old English)
            else -> Chalet
        }
    }

    fun getFontName(fontIndex: Int): String {
        return when (fontIndex) {
            0 -> "Font 0: Bank Gothic (Caps)"
            1 -> "Font 1: Standard (Chalet Sans)"
            2 -> "Font 2: Pricedown (GTA Title)"
            3 -> "Font 3: Diploma (Old English)"
            4 -> "Font 4: TXD Sprite"
            5 -> "Font 5: 3D Model Preview"
            else -> "Font $fontIndex"
        }
    }
}
