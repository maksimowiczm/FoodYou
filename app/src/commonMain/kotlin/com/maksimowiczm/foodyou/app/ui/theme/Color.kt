package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.theme.Theme
import com.maksimowiczm.foodyou.theme.ThemeContrast
import com.maksimowiczm.foodyou.theme.ThemeStyle
import com.materialkolor.hct.Hct

internal val MaterialDeepPurple = Color(0xFF6200EE)

private val ColorList =
    ((4..10) + (1..3)).map { it * 35.0 }.map { Color(Hct.from(it, 40.0, 40.0).toInt()) }

@Composable
internal fun rememberAvailableKeyColors(): List<Theme.Custom> = remember {
    listOf(
        Theme.Custom(
            seedColor = MaterialDeepPurple.value,
            style = ThemeStyle.TonalSpot,
            contrast = ThemeContrast.Default,
            isAmoled = false,
        ),
        Theme.Custom(
            seedColor = Color.Black.value,
            style = ThemeStyle.Monochrome,
            contrast = ThemeContrast.Default,
            isAmoled = true,
        ),
        Theme.Custom(
            seedColor = Color.Black.value,
            style = ThemeStyle.Monochrome,
            contrast = ThemeContrast.High,
            isAmoled = true,
        ),
    ) +
        ColorList.flatMap { color ->
            ThemeStyle.entries
                .filterNot { it == ThemeStyle.Monochrome }
                .map { style ->
                    Theme.Custom(
                        seedColor = color.value,
                        style = style,
                        contrast = ThemeContrast.Default,
                        isAmoled = false,
                    )
                }
        }
}
