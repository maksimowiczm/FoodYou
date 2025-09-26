package com.maksimowiczm.foodyou.app.ui.theme

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.maksimowiczm.foodyou.theme.Theme
import com.maksimowiczm.foodyou.theme.ThemeSettings
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun AdditionalSettings(
    themeSettings: ThemeSettings,
    onThemeSettingsChange: (ThemeSettings) -> Unit,
    modifier: Modifier,
) {
    var showCustomThemePicker by rememberSaveable { mutableStateOf(false) }
    if (showCustomThemePicker) {
        CustomThemePickerDialog(
            initialTheme = themeSettings.theme as? Theme.Custom,
            onConfirm = {
                onThemeSettingsChange(themeSettings.copy(theme = it))
                showCustomThemePicker = false
            },
            onDismiss = { showCustomThemePicker = false },
        )
    }

    val isDynamic = themeSettings.theme is Theme.Dynamic || themeSettings.theme is Theme.Default
    val themes = rememberThemes()

    Column(modifier) {
        ListItem(
            headlineContent = { Text(stringResource(Res.string.headline_custom_palette)) },
            modifier = Modifier.clickable { showCustomThemePicker = true },
            supportingContent = { Text(stringResource(Res.string.description_custom_palette)) },
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ListItem(
                headlineContent = { Text(stringResource(Res.string.headline_dynamic_colors)) },
                modifier =
                    Modifier.clickable {
                            if (isDynamic) {
                                onThemeSettingsChange(themeSettings.copy(theme = themes.first()))
                            } else {
                                onThemeSettingsChange(themeSettings.copy(theme = Theme.Dynamic))
                            }
                        }
                        .semantics { role = Role.Switch },
                supportingContent = { Text(stringResource(Res.string.description_dynamic_colors)) },
                trailingContent = { Switch(checked = isDynamic, onCheckedChange = null) },
            )
        }
    }
}
