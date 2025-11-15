package com.maksimowiczm.foodyou.app.ui.theme

import android.os.Build
import androidx.compose.foundation.clickable
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
actual fun ColumnScope.PlatformAdditionalSettings(
    themeSettings: ThemeSettings,
    onUpdateTheme: (Theme) -> Unit,
) {

    val isDynamic = themeSettings.theme is Theme.Dynamic || themeSettings.theme is Theme.Default
    val themes = rememberThemes()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ListItem(
            headlineContent = { Text(stringResource(Res.string.headline_dynamic_colors)) },
            modifier =
                Modifier.clickable {
                        if (isDynamic) onUpdateTheme(themes.first())
                        else onUpdateTheme(Theme.Dynamic)
                    }
                    .semantics { role = Role.Switch },
            supportingContent = { Text(stringResource(Res.string.description_dynamic_colors)) },
            trailingContent = { Switch(checked = isDynamic, onCheckedChange = null) },
        )
    }
}
