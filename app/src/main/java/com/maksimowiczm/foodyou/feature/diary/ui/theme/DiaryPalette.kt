package com.maksimowiczm.foodyou.feature.diary.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class DiaryPalette(
    val goalsFulfilledColor: Color = Color.Unspecified
)

val DarkDiaryPalette = DiaryPalette(
    goalsFulfilledColor = Color(0xFF00C853)
)

val LightDiaryPalette = DiaryPalette(
    goalsFulfilledColor = Color(0xFF00C853)
)

val LocalDiaryPalette = staticCompositionLocalOf { DiaryPalette() }
