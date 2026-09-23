package com.maksimowiczm.foodyou.features.personalization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.capabilities.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.component.ResetToDefaultDialog
import com.maksimowiczm.foodyou.shared.ui.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.utility.rememberContrastContentColor
import com.materialkolor.ktx.toHex
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

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        LocalNutrientsOrder.current
            .filter { it.isMacronutrient() }
            .forEachIndexed { index, order ->
                when (order) {
                    NutrientsOrder.Proteins ->
                        SegmentedListItem(
                            onClick = { proteinsDialog = true },
                            shapes = ListItemDefaults.segmentedShapes(index = index, count = 4),
                            content = { Text(stringResource(Res.string.nutriment_proteins)) },
                            supportingContent = { Text(proteinsColor.toHex()) },
                            colors =
                                ListItemDefaults.segmentedColors(
                                    containerColor = proteinsColor,
                                    contentColor = proteinsColor.rememberContrastContentColor(),
                                    supportingContentColor =
                                        proteinsColor.rememberContrastContentColor(),
                                ),
                        )

                    NutrientsOrder.Fats ->
                        SegmentedListItem(
                            onClick = { fatsDialog = true },
                            shapes = ListItemDefaults.segmentedShapes(index = index, count = 4),
                            content = { Text(stringResource(Res.string.nutriment_fats)) },
                            supportingContent = { Text(fatsColor.toHex()) },
                            colors =
                                ListItemDefaults.segmentedColors(
                                    containerColor = fatsColor,
                                    contentColor = fatsColor.rememberContrastContentColor(),
                                    supportingContentColor =
                                        fatsColor.rememberContrastContentColor(),
                                ),
                        )

                    NutrientsOrder.Carbohydrates ->
                        SegmentedListItem(
                            onClick = { carbsDialog = true },
                            shapes = ListItemDefaults.segmentedShapes(index = index, count = 4),
                            content = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                            supportingContent = { Text(carbsColor.toHex()) },
                            colors =
                                ListItemDefaults.segmentedColors(
                                    containerColor = carbsColor,
                                    contentColor = carbsColor.rememberContrastContentColor(),
                                    supportingContentColor =
                                        carbsColor.rememberContrastContentColor(),
                                ),
                        )

                    else -> Unit
                }
            }
        SegmentedListItem(
            onClick = { resetDialog = true },
            shapes = ListItemDefaults.segmentedShapes(index = 3, count = 4),
            content = { Text(stringResource(Res.string.headline_reset_to_default)) },
            colors =
                ListItemDefaults.segmentedColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
        )
    }
}

@Preview
@Composable
private fun NutrientsColorsPreview() {
    PreviewFoodYouTheme {
        val nutrientsPalette = LocalNutrientsPalette.current
        NutrientsColors(
            proteinsColor = nutrientsPalette.proteinsOnSurfaceContainer,
            onProteinsColorChange = {},
            carbsColor = nutrientsPalette.carbohydratesOnSurfaceContainer,
            onCarbsColorChange = {},
            fatsColor = nutrientsPalette.fatsOnSurfaceContainer,
            onFatsColorChange = {},
            onReset = {},
        )
    }
}
