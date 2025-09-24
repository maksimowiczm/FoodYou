package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.theme.ThemeSettings

@Composable
internal expect fun AdditionalSettings(
    themeSettings: ThemeSettings,
    onThemeSettingsChange: (ThemeSettings) -> Unit,
    modifier: Modifier = Modifier,
)
