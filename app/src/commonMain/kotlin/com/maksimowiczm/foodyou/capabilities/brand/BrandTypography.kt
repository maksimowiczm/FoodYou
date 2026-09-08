package com.maksimowiczm.foodyou.capabilities.brand

import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.Font

@Immutable data class BrandTypography(val displayMedium: TextStyle, val bodyLarge: TextStyle)

val Typography.brand: BrandTypography
    @Composable
    get() {
        val font =
            Font(
                Res.font.roboto_flex,
                variationSettings =
                    FontVariation.Settings(
                        FontVariation.weight(800),
                        FontVariation.slant(-10f),
                        FontVariation.width(150f),
                        FontVariation.grade(0),
                        FontVariation.Setting("XOPQ", 100f),
                    ),
            )

        val fontFamily = remember(font) { FontFamily(font) }

        fun TextStyle.brand(weight: FontWeight) = copy(fontFamily = fontFamily, fontWeight = weight)

        return remember(this, fontFamily) {
            BrandTypography(
                displayMedium = displayMedium.brand(FontWeight.W900),
                bodyLarge = bodyLarge.brand(FontWeight.W800),
            )
        }
    }
