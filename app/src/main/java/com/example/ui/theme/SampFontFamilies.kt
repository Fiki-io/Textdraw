package com.example.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

object SampFontFamilies {
    // Font 0: Authentic SA-MP Diploma Old English (GTA San Andreas calligraphy)
    val Diploma = FontFamily(
        Font(R.font.diploma, FontWeight.Normal)
    )

    // Font 1: Standard SA-MP Chalet London / Grotesque
    val Chalet = FontFamily(
        Font(R.font.chivo, FontWeight.Normal)
    )

    // Font 2: Authentic SA-MP Bank Gothic (GTA San Andreas vehicle & area names)
    val BankGothic = FontFamily(
        Font(R.font.bebas_neue, FontWeight.Bold)
    )

    // Font 3: Authentic GTA San Andreas Pricedown (GTA Title font / money / numbers / stats)
    val Pricedown = FontFamily(
        Font(R.font.pricedown, FontWeight.Normal)
    )

    fun getFontFamily(fontIndex: Int): FontFamily {
        return when (fontIndex) {
            0 -> Diploma
            1 -> Chalet
            2 -> BankGothic
            3 -> Pricedown
            else -> Chalet
        }
    }

    fun getFontName(fontIndex: Int): String {
        return when (fontIndex) {
            0 -> "Font 0: Diploma (Old English)"
            1 -> "Font 1: Standard (Chalet Sans)"
            2 -> "Font 2: Bank Gothic (Caps)"
            3 -> "Font 3: Pricedown (GTA Title)"
            4 -> "Font 4: TXD Sprite"
            5 -> "Font 5: 3D Model Preview"
            else -> "Font $fontIndex"
        }
    }
}
