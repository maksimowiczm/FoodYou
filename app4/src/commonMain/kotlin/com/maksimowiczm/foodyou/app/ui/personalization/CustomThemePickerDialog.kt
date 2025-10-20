package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeContrast
import com.maksimowiczm.foodyou.device.domain.ThemeStyle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomThemePickerDialog(
    initialTheme: Theme.Custom?,
    onConfirm: (Theme.Custom) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialColor = initialTheme?.seedColor?.let { Color(it) }
    val colorPickerState = rememberColorPickerState(initialColor)

    val paletteExpanded = rememberSaveable { mutableStateOf(false) }
    val paletteStyle = rememberSaveable {
        mutableStateOf(initialTheme?.style ?: ThemeStyle.TonalSpot)
    }
    val contrastExpanded = rememberSaveable { mutableStateOf(false) }
    val contrast = rememberSaveable {
        mutableStateOf(initialTheme?.contrast ?: ThemeContrast.Default)
    }
    val isAmoled = rememberSaveable { mutableStateOf(initialTheme?.isAmoled ?: false) }

    val content =
        @Composable {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(Res.string.description_custom_palette))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ColorPicker(
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        state = colorPickerState,
                    )
                    TextField(
                        value = paletteStyle.value.name,
                        onValueChange = {},
                        trailingIcon = {
                            IconButton(onClick = { paletteExpanded.value = true }) {
                                Icon(Icons.Outlined.ArrowDropDown, null)
                            }
                            DropdownMenu(
                                expanded = paletteExpanded.value,
                                onDismissRequest = { paletteExpanded.value = false },
                            ) {
                                ThemeStyle.entries.forEach { style ->
                                    DropdownMenuItem(
                                        text = { Text(style.name) },
                                        onClick = {
                                            paletteStyle.value = style
                                            paletteExpanded.value = false
                                        },
                                    )
                                }
                            }
                        },
                        readOnly = true,
                        label = { Text(stringResource(Res.string.headline_palette_style)) },
                    )
                    TextField(
                        value = contrast.value.name,
                        onValueChange = {},
                        trailingIcon = {
                            IconButton(onClick = { contrastExpanded.value = true }) {
                                Icon(Icons.Outlined.ArrowDropDown, null)
                            }
                            DropdownMenu(
                                expanded = contrastExpanded.value,
                                onDismissRequest = { contrastExpanded.value = false },
                            ) {
                                ThemeContrast.entries.forEach { contrastOption ->
                                    DropdownMenuItem(
                                        text = { Text(contrastOption.name) },
                                        onClick = {
                                            contrast.value = contrastOption
                                            contrastExpanded.value = false
                                        },
                                    )
                                }
                            }
                        },
                        readOnly = true,
                        label = { Text(stringResource(Res.string.headline_contrast)) },
                    )
                }
                Row(
                    modifier =
                        Modifier.clickable { isAmoled.value = !isAmoled.value }
                            .semantics { role = Role.Checkbox }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Checkbox(checked = isAmoled.value, onCheckedChange = null)
                    Text(stringResource(Res.string.headline_amoled))
                }
            }
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.headline_custom_palette)) },
        text = content,
        confirmButton = {
            TextButton(
                onClick = {
                    val color = colorPickerState.selectedColor
                    onConfirm(
                        Theme.Custom(
                            seedColor = color.value,
                            style = paletteStyle.value,
                            contrast = contrast.value,
                            isAmoled = isAmoled.value,
                        )
                    )
                }
            ) {
                Text(stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        },
        modifier = modifier,
    )
}

@Composable
fun ColorPickerDialog(
    initialColor: Color?,
    onConfirm: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorPickerState = rememberColorPickerState(initialColor)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.headline_pick_a_color)) },
        text = {
            ColorPicker(modifier = Modifier.fillMaxWidth().height(32.dp), state = colorPickerState)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(colorPickerState.selectedColor) }) {
                Text(stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onDismissRequest) { Text(stringResource(Res.string.action_cancel)) }
        },
        modifier = modifier,
    )
}
