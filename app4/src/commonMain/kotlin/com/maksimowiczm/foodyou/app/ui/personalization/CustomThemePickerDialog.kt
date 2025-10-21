package com.maksimowiczm.foodyou.app.ui.personalization

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalClipboardManager
import com.maksimowiczm.foodyou.device.domain.Theme
import com.maksimowiczm.foodyou.device.domain.ThemeContrast
import com.maksimowiczm.foodyou.device.domain.ThemeStyle
import com.materialkolor.ktx.toHex
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomThemePickerDialog(
    initialTheme: Theme.Custom?,
    onConfirm: (Theme.Custom) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current

    val initialColor = initialTheme?.seedColor?.let { Color(it) }
    val controller = rememberColorPickerController()
    LaunchedEffect(Unit) { initialColor?.let { controller.selectByColor(it, false) } }

    var paletteExpanded by rememberSaveable { mutableStateOf(false) }
    val paletteStyle = rememberSaveable {
        mutableStateOf(initialTheme?.style ?: ThemeStyle.TonalSpot)
    }
    var contrastExpanded by rememberSaveable { mutableStateOf(false) }
    val contrast = rememberSaveable {
        mutableStateOf(initialTheme?.contrast ?: ThemeContrast.Default)
    }
    val isAmoled = rememberSaveable { mutableStateOf(initialTheme?.isAmoled ?: false) }

    val content =
        @Composable {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(Res.string.description_custom_palette))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        HsvColorPicker(
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                            controller = controller,
                        )
                        BrightnessSlider(
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            controller = controller,
                        )
                        Text(
                            text = controller.selectedColor.value.toHex(),
                            color = controller.selectedColor.value,
                            style = MaterialTheme.typography.labelLargeEmphasized,
                            modifier =
                                Modifier.clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        clipboardManager.copy(
                                            "Color",
                                            controller.selectedColor.value.toHex(),
                                        )
                                    },
                                ),
                        )
                    }
                    TextField(
                        value = paletteStyle.value.name,
                        onValueChange = {},
                        trailingIcon = {
                            Icon(Icons.Outlined.ArrowDropDown, null)
                            DropdownMenu(
                                expanded = paletteExpanded,
                                onDismissRequest = { paletteExpanded = false },
                            ) {
                                ThemeStyle.entries.forEach { style ->
                                    DropdownMenuItem(
                                        text = { Text(style.name) },
                                        onClick = {
                                            paletteStyle.value = style
                                            paletteExpanded = false
                                        },
                                    )
                                }
                            }
                        },
                        readOnly = true,
                        label = { Text(stringResource(Res.string.headline_palette_style)) },
                        interactionSource =
                            remember { MutableInteractionSource() }
                                .apply {
                                    LaunchedEffect(Unit) {
                                        interactions
                                            .filter { it is PressInteraction.Release }
                                            .collectLatest { paletteExpanded = true }
                                    }
                                },
                    )
                    TextField(
                        value = contrast.value.name,
                        onValueChange = {},
                        trailingIcon = {
                            Icon(Icons.Outlined.ArrowDropDown, null)
                            DropdownMenu(
                                expanded = contrastExpanded,
                                onDismissRequest = { contrastExpanded = false },
                            ) {
                                ThemeContrast.entries.forEach { contrastOption ->
                                    DropdownMenuItem(
                                        text = { Text(contrastOption.name) },
                                        onClick = {
                                            contrast.value = contrastOption
                                            contrastExpanded = false
                                        },
                                    )
                                }
                            }
                        },
                        readOnly = true,
                        label = { Text(stringResource(Res.string.headline_contrast)) },
                        interactionSource =
                            remember { MutableInteractionSource() }
                                .apply {
                                    LaunchedEffect(Unit) {
                                        interactions
                                            .filter { it is PressInteraction.Release }
                                            .collectLatest { contrastExpanded = true }
                                    }
                                },
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
                    onConfirm(
                        Theme.Custom(
                            seedColor = controller.selectedColor.value.value,
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
    val clipboardManager = LocalClipboardManager.current

    val controller = rememberColorPickerController()
    LaunchedEffect(Unit) { initialColor?.let { controller.selectByColor(it, false) } }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.headline_pick_a_color)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HsvColorPicker(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    controller = controller,
                )
                BrightnessSlider(
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    controller = controller,
                )
                Text(
                    text = controller.selectedColor.value.toHex(),
                    color = controller.selectedColor.value,
                    style = MaterialTheme.typography.labelLargeEmphasized,
                    modifier =
                        Modifier.clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = {
                                clipboardManager.copy(
                                    "Color",
                                    controller.selectedColor.value.toHex(),
                                )
                            },
                        ),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(controller.selectedColor.value) }) {
                Text(stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onDismissRequest) { Text(stringResource(Res.string.action_cancel)) }
        },
        modifier = modifier,
    )
}
