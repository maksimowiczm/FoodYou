package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.ThemeOption
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ThemePicker(
    themeOption: ThemeOption,
    onThemeOptionChange: (ThemeOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement =
            Arrangement.spacedBy(
                ButtonGroupDefaults.ConnectedSpaceBetween,
                Alignment.CenterHorizontally,
            ),
    ) {
        TonalToggleButton(
            checked = themeOption == ThemeOption.System,
            onCheckedChange = { onThemeOptionChange(ThemeOption.System) },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
            content = { Text(stringResource(Res.string.headline_system)) },
        )
        TonalToggleButton(
            checked = themeOption == ThemeOption.Light,
            onCheckedChange = { onThemeOptionChange(ThemeOption.Light) },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
            content = { Text(stringResource(Res.string.headline_light)) },
        )
        TonalToggleButton(
            checked = themeOption == ThemeOption.Dark,
            onCheckedChange = { onThemeOptionChange(ThemeOption.Dark) },
            modifier = Modifier.semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
            content = { Text(stringResource(Res.string.headline_dark)) },
        )
    }
}
