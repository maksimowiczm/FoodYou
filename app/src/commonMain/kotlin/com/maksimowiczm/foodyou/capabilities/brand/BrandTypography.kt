package com.maksimowiczm.foodyou.capabilities.brand

import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.Font

@Immutable
data class BrandTypography(
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val titleLarge: TextStyle,
    val bodyLarge: TextStyle,
)

val Typography.brand: BrandTypography
    @Composable
    get() {
        val w900 = brandFont(FontWeight.W900)
        val w800 = brandFont(FontWeight.W800)
        val fontFamily = remember(w900, w800) { FontFamily(w900, w800) }

        return remember(this, fontFamily) {
            BrandTypography(
                displayMedium =
                    displayMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.W900),
                displaySmall =
                    displaySmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.W900),
                titleLarge = titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.W800),
                bodyLarge = bodyLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.W800),
            )
        }
    }

@Composable
private fun brandFont(weight: FontWeight) =
    Font(
        Res.font.roboto_flex,
        weight = weight,
        variationSettings =
            FontVariation.Settings(
                FontVariation.weight(weight.weight),
                FontVariation.slant(-10f),
                FontVariation.width(150f),
                FontVariation.grade(0),
                FontVariation.Setting("XOPQ", 100f),
            ),
    )
