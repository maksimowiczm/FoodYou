package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeSettings
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdditionalSettings(
    themeSettings: ThemeSettings,
    onRandomizeTheme: (Boolean) -> Unit,
    onUpdateTheme: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val settingsCount = remember { platformAdditionalSettingsCount() }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        PlatformAdditionalSettings(themeSettings = themeSettings, onUpdateTheme = onUpdateTheme)
        SegmentedListItem(
            checked = themeSettings.randomizeOnLaunch,
            onCheckedChange = { onRandomizeTheme(!themeSettings.randomizeOnLaunch) },
            shapes =
                ListItemDefaults.segmentedShapes(index = settingsCount, count = settingsCount + 1),
            trailingContent = {
                Switch(checked = themeSettings.randomizeOnLaunch, onCheckedChange = null)
            },
            verticalAlignment = Alignment.CenterVertically,
            supportingContent = { Text(stringResource(Res.string.description_random_theme)) },
            colors =
                ListItemDefaults.segmentedColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
            content = { Text(stringResource(Res.string.headline_random_theme)) },
        )
    }
}

internal expect fun platformAdditionalSettingsCount(): Int

@Composable
expect fun ColumnScope.PlatformAdditionalSettings(
    themeSettings: ThemeSettings,
    onUpdateTheme: (Theme) -> Unit,
)
