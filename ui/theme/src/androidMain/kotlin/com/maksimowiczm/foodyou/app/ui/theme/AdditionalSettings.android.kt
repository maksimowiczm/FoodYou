package com.maksimowiczm.foodyou.app.ui.theme

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.Theme
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.ThemeSettings
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
                            onThemeSettingsChange(themeSettings.copy(theme = Theme.Dynamic))
                        }
                        .semantics { role = Role.Switch },
                supportingContent = { Text(stringResource(Res.string.description_dynamic_colors)) },
                trailingContent = {
                    Switch(checked = themeSettings.isDynamic, onCheckedChange = null)
                },
            )
        }
    }
}
