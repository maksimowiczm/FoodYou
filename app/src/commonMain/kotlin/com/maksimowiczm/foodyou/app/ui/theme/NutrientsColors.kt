package com.maksimowiczm.foodyou.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.component.ResetToDefaultDialog
import com.maksimowiczm.foodyou.common.compose.extension.horizontal
import com.maksimowiczm.foodyou.common.compose.extension.vertical
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NutrientsColors(
    proteinsColor: Color,
    onProteinsColorChange: (Color) -> Unit,
    carbsColor: Color,
    onCarbsColorChange: (Color) -> Unit,
    fatsColor: Color,
    onFatsColorChange: (Color) -> Unit,
    onReset: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var proteinsDialog by rememberSaveable { mutableStateOf(false) }
    if (proteinsDialog) {
        ColorPickerDialog(
            initialColor = proteinsColor,
            onConfirm = {
                onProteinsColorChange(it)
                proteinsDialog = false
            },
            onDismissRequest = { proteinsDialog = false },
        )
    }

    var carbsDialog by rememberSaveable { mutableStateOf(false) }
    if (carbsDialog) {
        ColorPickerDialog(
            initialColor = carbsColor,
            onConfirm = {
                onCarbsColorChange(it)
                carbsDialog = false
            },
            onDismissRequest = { carbsDialog = false },
        )
    }

    var fatsDialog by rememberSaveable { mutableStateOf(false) }
    if (fatsDialog) {
        ColorPickerDialog(
            initialColor = fatsColor,
            onConfirm = {
                onFatsColorChange(it)
                fatsDialog = false
            },
            onDismissRequest = { fatsDialog = false },
        )
    }

    var resetDialog by rememberSaveable { mutableStateOf(false) }
    if (resetDialog) {
        ResetToDefaultDialog(
            onConfirm = {
                onReset()
                resetDialog = false
            },
            onDismissRequest = { resetDialog = false },
        ) {
            Text(stringResource(Res.string.description_nutrients_colors_reset))
        }
    }

    Column(modifier = modifier.padding(contentPadding.vertical())) {
        Text(
            text = stringResource(Res.string.headline_nutrients_colors),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(contentPadding.horizontal()),
        )
        ListItem(
            headlineContent = { Text(stringResource(Res.string.nutriment_proteins)) },
            modifier = Modifier.clickable { proteinsDialog = true },
            leadingContent = {
                Canvas(Modifier.size(24.dp).clip(MaterialTheme.shapes.small)) {
                    drawRect(proteinsColor)
                }
            },
            supportingContent = { Text(proteinsColor.value.toColorString()) },
        )
        ListItem(
            headlineContent = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
            modifier = Modifier.clickable { carbsDialog = true },
            leadingContent = {
                Canvas(Modifier.size(24.dp).clip(MaterialTheme.shapes.small)) {
                    drawRect(carbsColor)
                }
            },
            supportingContent = { Text(carbsColor.value.toColorString()) },
        )
        ListItem(
            headlineContent = { Text(stringResource(Res.string.nutriment_fats)) },
            modifier = Modifier.clickable { fatsDialog = true },
            leadingContent = {
                Canvas(Modifier.size(24.dp).clip(MaterialTheme.shapes.small)) {
                    drawRect(fatsColor)
                }
            },
            supportingContent = { Text(fatsColor.value.toColorString()) },
        )
        ListItem(
            headlineContent = { Text(stringResource(Res.string.headline_reset_to_default)) },
            modifier = Modifier.clickable { resetDialog = true },
        )
    }
}

private fun ULong.toColorString(): String = "#${this.toString(16).substring(0, 8).uppercase()}"
