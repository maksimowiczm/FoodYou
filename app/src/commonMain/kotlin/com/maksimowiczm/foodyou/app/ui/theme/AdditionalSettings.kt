package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.maksimowiczm.foodyou.theme.Theme
import com.maksimowiczm.foodyou.theme.ThemeSettings
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdditionalSettings(
    themeSettings: ThemeSettings,
    onRandomizeTheme: (Boolean) -> Unit,
    onUpdateTheme: (Theme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        PlatformAdditionalSettings(themeSettings = themeSettings, onUpdateTheme = onUpdateTheme)
        ListItem(
            headlineContent = { Text(stringResource(Res.string.headline_random_theme)) },
            modifier =
                Modifier.clickable { onRandomizeTheme(!themeSettings.randomizeOnLaunch) }
                    .semantics { role = Role.Switch },
            supportingContent = { Text(stringResource(Res.string.description_random_theme)) },
            trailingContent = {
                Switch(checked = themeSettings.randomizeOnLaunch, onCheckedChange = null)
            },
        )
    }
}

@Composable
expect fun ColumnScope.PlatformAdditionalSettings(
    themeSettings: ThemeSettings,
    onUpdateTheme: (Theme) -> Unit,
)
