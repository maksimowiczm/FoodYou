package com.maksimowiczm.foodyou.app.ui.sponsor

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.Font

@Immutable internal class SponsorTypography(val title: TextStyle, val goal: TextStyle)

internal val sponsorTypography: SponsorTypography
    @Composable
    get() {
        val title =
            FontFamily(
                Font(
                    Res.font.roboto_flex,
                    variationSettings =
                        FontVariation.Settings(
                            FontVariation.weight(800),
                            FontVariation.width(150f),
                            FontVariation.grade(0),
                            FontVariation.Setting("XOPQ", 100f),
                        ),
                )
            )

        val goal =
            FontFamily(
                Font(
                    Res.font.roboto_flex,
                    variationSettings =
                        FontVariation.Settings(
                            FontVariation.slant(-10f),
                            FontVariation.weight(800),
                            FontVariation.width(100f),
                            FontVariation.grade(0),
                            FontVariation.Setting("XOPQ", 100f),
                        ),
                )
            )

        return with(MaterialTheme.typography) {
            SponsorTypography(
                title = headlineLarge.copy(fontFamily = title, fontWeight = FontWeight.W800),
                goal = headlineLarge.copy(fontFamily = goal, fontWeight = FontWeight.W800),
            )
        }
    }
