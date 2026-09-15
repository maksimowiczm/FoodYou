package com.maksimowiczm.foodyou.features.personalization

import android.os.Build
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalHapticFeedback
import com.maksimowiczm.foodyou.preferences.domain.Theme
import com.maksimowiczm.foodyou.preferences.domain.ThemePreference
import com.maksimowiczm.foodyou.shared.ui.extension.toggle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ColumnScope.PlatformAdditionalSettings(
    themeSettings: ThemePreference,
    onUpdateTheme: (Theme) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val settingsCount = remember { platformAdditionalSettingsCount() }

    val isDynamic = themeSettings.theme is Theme.Dynamic || themeSettings.theme is Theme.Default
    val themes = rememberThemes()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SegmentedListItem(
            checked = isDynamic,
            onCheckedChange = {
                hapticFeedback.toggle(!isDynamic)
                if (isDynamic) onUpdateTheme(themes.first()) else onUpdateTheme(Theme.Dynamic)
            },
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = settingsCount + 1),
            trailingContent = { Switch(checked = isDynamic, onCheckedChange = null) },
            supportingContent = { Text(stringResource(Res.string.description_dynamic_colors)) },
            colors =
                ListItemDefaults.segmentedColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
            content = { Text(stringResource(Res.string.headline_dynamic_colors)) },
        )
    }
}

internal actual fun platformAdditionalSettingsCount(): Int =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 1 else 0
