package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.stringResource
import com.maksimowiczm.foodyou.core.ui.component.BarcodeScannerIconButton
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductForm(
    state: ProductFormState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    if (showBarcodeScanner) {
        FullScreenCameraBarcodeScanner(
            onBarcodeScan = {
                state.barcode.textFieldState.setTextAndPlaceCursorAtEnd(it)
                showBarcodeScanner = false
            },
            onClose = {
                showBarcodeScanner = false
            }
        )
    }

    val layoutDirection = LocalLayoutDirection.current
    val horizontalPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection),
        end = contentPadding.calculateStartPadding(layoutDirection)
    )
    val verticalPadding = PaddingValues(
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding()
    )

    Column(
        modifier = modifier.padding(verticalPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.headline_general),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.name.TextField(
            label = stringResource(Res.string.product_name),
            required = true,
            suffix = null,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.brand.TextField(
            label = stringResource(Res.string.product_brand),
            suffix = null,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        TextField(
            state = state.barcode.textFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontalPadding),
            label = { Text(stringResource(Res.string.product_barcode)) },
            trailingIcon = {
                BarcodeScannerIconButton(
                    onClick = { showBarcodeScanner = true }
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    state.isLiquid = !state.isLiquid
                }
                .padding(vertical = 8.dp)
                .padding(horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
                content = {
                    Checkbox(
                        checked = state.isLiquid,
                        onCheckedChange = null
                    )
                }
            )
            Column {
                Text(
                    text = stringResource(Res.string.action_treat_as_liquid),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.description_treat_as_liquid),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ValuesPerPicker(
            isLiquid = state.isLiquid,
            measurement = state.measurement,
            onValueChange = { state.measurement = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontalPadding)
        )

        state.packageWeight.TextField(
            label = stringResource(Res.string.product_package_weight),
            required = state.measurement is Measurement.Package,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            suffix = if (state.isLiquid) {
                stringResource(Res.string.unit_milliliter_short)
            } else {
                stringResource(Res.string.unit_gram_short)
            }
        )

        state.servingWeight.TextField(
            label = stringResource(Res.string.product_serving_weight),
            required = state.measurement is Measurement.Serving,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            suffix = if (state.isLiquid) {
                stringResource(Res.string.unit_milliliter_short)
            } else {
                stringResource(Res.string.unit_gram_short)
            }
        )

        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(Res.string.headline_macronutrients),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(Res.string.neutral_calories_are_calculated),
                style = MaterialTheme.typography.bodySmall
            )
        }

        state.proteins.TextField(
            label = stringResource(Res.string.nutriment_proteins),
            required = true,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.carbohydrates.TextField(
            label = stringResource(Res.string.nutriment_carbohydrates),
            required = true,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.fats.TextField(
            label = stringResource(Res.string.nutriment_fats),
            required = true,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.calories.TextField(
            label = stringResource(Res.string.unit_calories),
            required = true,
            suffix = stringResource(Res.string.unit_kcal),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.nutriment_fats),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.saturatedFats.TextField(
            label = stringResource(Res.string.nutriment_saturated_fats),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.monounsaturatedFats.TextField(
            label = stringResource(Res.string.nutriment_monounsaturated_fats),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.polyunsaturatedFats.TextField(
            label = stringResource(Res.string.nutriment_polyunsaturated_fats),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.omega3.TextField(
            label = stringResource(Res.string.nutriment_omega_3),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.omega6.TextField(
            label = stringResource(Res.string.nutriment_omega_6),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.headline_other),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.sugars.TextField(
            label = stringResource(Res.string.nutriment_sugars),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.salt.TextField(
            label = stringResource(Res.string.nutriment_salt),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.fiber.TextField(
            label = stringResource(Res.string.nutriment_fiber),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.cholesterolMilli.TextField(
            label = stringResource(Res.string.nutriment_cholesterol),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.caffeineMilli.TextField(
            label = stringResource(Res.string.nutriment_caffeine),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.headline_vitamins),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminAMicro.TextField(
            label = stringResource(Res.string.vitamin_a),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB1Milli.TextField(
            label = stringResource(Res.string.vitamin_b1),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB2Milli.TextField(
            label = stringResource(Res.string.vitamin_b2),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB3Milli.TextField(
            label = stringResource(Res.string.vitamin_b3),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB5Milli.TextField(
            label = stringResource(Res.string.vitamin_b5),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB6Milli.TextField(
            label = stringResource(Res.string.vitamin_b6),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB7Micro.TextField(
            label = stringResource(Res.string.vitamin_b7),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB9Micro.TextField(
            label = stringResource(Res.string.vitamin_b9),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminB12Micro.TextField(
            label = stringResource(Res.string.vitamin_b12),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminCMilli.TextField(
            label = stringResource(Res.string.vitamin_c),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminDMicro.TextField(
            label = stringResource(Res.string.vitamin_d),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminEMilli.TextField(
            label = stringResource(Res.string.vitamin_e),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.vitaminKMicro.TextField(
            label = stringResource(Res.string.vitamin_k),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.headline_minerals),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.manganeseMilli.TextField(
            label = stringResource(Res.string.mineral_manganese),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.magnesiumMilli.TextField(
            label = stringResource(Res.string.mineral_magnesium),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.potassiumMilli.TextField(
            label = stringResource(Res.string.mineral_potassium),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.calciumMilli.TextField(
            label = stringResource(Res.string.mineral_calcium),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.copperMilli.TextField(
            label = stringResource(Res.string.mineral_copper),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.zincMilli.TextField(
            label = stringResource(Res.string.mineral_zinc),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.sodiumMilli.TextField(
            label = stringResource(Res.string.mineral_sodium),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.ironMilli.TextField(
            label = stringResource(Res.string.mineral_iron),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.phosphorusMilli.TextField(
            label = stringResource(Res.string.mineral_phosphorus),
            suffix = stringResource(Res.string.unit_milligram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.seleniumMicro.TextField(
            label = stringResource(Res.string.mineral_selenium),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.iodineMicro.TextField(
            label = stringResource(Res.string.mineral_iodine),
            suffix = stringResource(Res.string.unit_microgram_short),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.chromiumMicro.TextField(
            label = stringResource(Res.string.mineral_chromium),
            suffix = stringResource(Res.string.unit_microgram_short),
            imeAction = ImeAction.Done,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        Text(
            text = stringResource(Res.string.headline_extra),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        TextField(
            state = state.note.textFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontalPadding),
            label = { Text(stringResource(Res.string.headline_note)) },
            supportingText = { Text(stringResource(Res.string.description_add_note)) }
        )
    }
}

@Composable
internal inline fun <reified T> FormField<T, ProductFormFieldError>.TextField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    imeAction: ImeAction? = null,
    suffix: String? = stringResource(Res.string.unit_gram_short)
) {
    TextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        supportingText = {
            val error = this.error
            if (error != null) {
                Text(error.stringResource())
            } else if (required) {
                Text(stringResource(Res.string.neutral_required))
            }
        },
        suffix = suffix?.let { { Text(suffix) } },
        isError = error != null,
        keyboardOptions = if (T::class == Float::class) {
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = imeAction ?: ImeAction.Next
            )
        } else {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction ?: ImeAction.Next
            )
        }
    )
}

@Composable
private fun ValuesPerPicker(
    isLiquid: Boolean,
    measurement: Measurement,
    onValueChange: (Measurement) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val possibleValues = remember(isLiquid) {
        listOf(
            if (isLiquid) Measurement.Milliliter(100f) else Measurement.Gram(100f),
            Measurement.Serving(1f),
            Measurement.Package(1f)
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(stringResource(Res.string.headline_values_per))

        Box {
            TextButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                if (measurement is Measurement.Gram && isLiquid) {
                    Text(
                        100f.formatClipZeros() + " " +
                            stringResource(Res.string.unit_milliliter_short)
                    )
                } else {
                    Text(measurement.stringResource())
                }
                Spacer(Modifier.width(8.dp))

                val animatedRotation by animateFloatAsState(
                    targetValue = if (expanded) 90f else 0f,
                    animationSpec = tween(150)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = animatedRotation
                    }
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                possibleValues.forEach {
                    DropdownMenuItem(
                        text = { Text(it.stringResource()) },
                        onClick = {
                            expanded = false
                            onValueChange(it)
                        }
                    )
                }
            }
        }
    }
}
