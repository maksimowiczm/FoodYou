package com.maksimowiczm.foodyou.app.ui.personalization

import android.os.Build
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ColumnScope.PlatformAdditionalSettings(
    themeSettings: ThemeSettings,
    onUpdateTheme: (Theme) -> Unit,
) {
    val settingsCount = remember { platformAdditionalSettingsCount() }

    val isDynamic = themeSettings.theme is Theme.Dynamic || themeSettings.theme is Theme.Default
    val themes = rememberThemes()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        SegmentedListItem(
            checked = isDynamic,
            onCheckedChange = {
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
