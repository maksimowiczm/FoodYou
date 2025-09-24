package com.maksimowiczm.foodyou.app.ui.about

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.Font

@Immutable internal data class AboutTypography(val brandName: TextStyle)

internal val aboutTypography: AboutTypography
    @Composable
    get() {
        val fontFamily =
            FontFamily(
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
            )

        return with(MaterialTheme.typography) {
            AboutTypography(
                brandName =
                    displayMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.W900)
            )
        }
    }
