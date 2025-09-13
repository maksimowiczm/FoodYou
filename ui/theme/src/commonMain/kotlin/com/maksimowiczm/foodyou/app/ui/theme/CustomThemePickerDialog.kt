package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.Theme
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.ThemeContrast
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.ThemeStyle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CustomThemePickerDialog(
    initialTheme: Theme.Custom?,
    onConfirm: (Theme.Custom) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var color = remember { mutableStateOf(initialTheme?.seedColor?.let { Color(it) }) }

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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ColorPickerTextField(
                        label = { Text(stringResource(Res.string.headline_seed_color)) },
                        placeholder = { Text("#FF6750A4") },
                        initialColor = initialTheme?.seedColor?.let(::Color),
                        onColorChange = { color.value = it },
                        modifier = Modifier.fillMaxWidth(),
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
                    val color = color.value
                    if (color != null) {
                        onConfirm(
                            Theme.Custom(
                                seedColor = color.value,
                                style = paletteStyle.value,
                                contrast = contrast.value,
                                isAmoled = isAmoled.value,
                            )
                        )
                    }
                },
                enabled = color.value != null,
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
internal fun ColorPickerDialog(
    initialColor: Color?,
    onConfirm: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var color by remember { mutableStateOf(initialColor) }

    val content =
        @Composable {
            ColorPickerTextField(
                label = { Text(stringResource(Res.string.headline_color)) },
                placeholder = { Text("#FF6750A4") },
                initialColor = initialColor,
                onColorChange = { color = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.headline_pick_a_color)) },
        text = content,
        confirmButton = {
            TextButton(
                onClick = {
                    val color = color
                    if (color != null) {
                        onConfirm(color)
                    }
                },
                enabled = color != null,
            ) {
                Text(stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onDismissRequest) { Text(stringResource(Res.string.action_cancel)) }
        },
        modifier = modifier,
    )
}

@Composable
internal fun ColorPickerTextField(
    label: @Composable TextFieldLabelScope.() -> Unit,
    placeholder: @Composable () -> Unit,
    initialColor: Color?,
    onColorChange: (Color?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorState =
        rememberTextFieldState(
            initialColor?.value?.toString(16)?.substring(0, 8)?.uppercase() ?: ""
        )
    val parsedColor =
        remember {
                derivedStateOf {
                    if (colorState.text.length != 8) return@derivedStateOf null

                    runCatching {
                            val long = colorState.text.toString().removePrefix("#").toLong(16)
                            Color(long)
                        }
                        .getOrNull()
                }
            }
            .value

    LaunchedEffect(parsedColor) { onColorChange(parsedColor) }

    val isValid = parsedColor != null && colorState.text.length == 8

    TextField(
        state = colorState,
        label = label,
        placeholder = placeholder,
        trailingIcon = {
            if (parsedColor != null) {
                Canvas(Modifier.size(24.dp)) { drawCircle(parsedColor) }
            }
        },
        isError = !isValid && colorState.text.isNotEmpty(),
        modifier = modifier,
    )
}
