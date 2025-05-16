package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.FullScreenDialog
import com.maksimowiczm.foodyou.core.ui.res.stringResource
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.feature.barcodescanner.CameraBarcodeScannerScreen
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductForm(state: ProductFormState, modifier: Modifier = Modifier) {
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    if (showBarcodeScanner) {
        FullScreenDialog(
            onDismissRequest = { showBarcodeScanner = false }
        ) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    state.barcode.textFieldState.setTextAndPlaceCursorAtEnd(it)
                    showBarcodeScanner = false
                },
                onClose = {
                    showBarcodeScanner = false
                }
            )
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.headline_general),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        state.name.TextField(
            label = stringResource(Res.string.product_name),
            required = true,
            suffix = null
        )

        state.brand.TextField(
            label = stringResource(Res.string.product_brand),
            suffix = null
        )

        TextField(
            state = state.barcode.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.product_barcode)) },
            trailingIcon = {
                IconButton(
                    onClick = { showBarcodeScanner = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        ValuesPerPicker(
            measurement = state.measurement,
            onValueChange = { state.measurement = it },
            modifier = Modifier.fillMaxWidth()
        )

        state.packageWeight.TextField(
            label = stringResource(Res.string.product_package_weight),
            required = state.measurement is Measurement.Package
        )

        state.servingWeight.TextField(
            label = stringResource(Res.string.product_serving_weight),
            required = state.measurement is Measurement.Serving
        )

        Column(
            modifier = Modifier.padding(vertical = 8.dp),
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
            required = true
        )

        state.carbohydrates.TextField(
            label = stringResource(Res.string.nutriment_carbohydrates),
            required = true
        )

        state.fats.TextField(
            label = stringResource(Res.string.nutriment_fats),
            required = true
        )

        state.calories.TextField(
            label = stringResource(Res.string.unit_calories),
            required = true,
            suffix = stringResource(Res.string.unit_kcal)
        )

        Text(
            text = stringResource(Res.string.nutriment_fats),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        state.saturatedFats.TextField(
            label = stringResource(Res.string.nutriment_saturated_fats)
        )

        state.monounsaturatedFats.TextField(
            label = stringResource(Res.string.nutriment_monounsaturated_fats)
        )

        state.polyunsaturatedFats.TextField(
            label = stringResource(Res.string.nutriment_polyunsaturated_fats)
        )

        state.omega3.TextField(
            label = stringResource(Res.string.nutriment_omega_3)
        )

        state.omega6.TextField(
            label = stringResource(Res.string.nutriment_omega_6)
        )

        Text(
            text = stringResource(Res.string.headline_other),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        state.sugars.TextField(
            label = stringResource(Res.string.nutriment_sugars)
        )

        state.salt.TextField(
            label = stringResource(Res.string.nutriment_salt)
        )

        state.fiber.TextField(
            label = stringResource(Res.string.nutriment_fiber)
        )

        state.cholesterolMilli.TextField(
            label = stringResource(Res.string.nutriment_cholesterol),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.caffeineMilli.TextField(
            label = stringResource(Res.string.nutriment_caffeine),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        Text(
            text = stringResource(Res.string.headline_vitamins),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        state.vitaminAMicro.TextField(
            label = stringResource(Res.string.vitamin_a),
            suffix = stringResource(Res.string.unit_microgram_short)
        )

        state.vitaminB1Milli.TextField(
            label = stringResource(Res.string.vitamin_b1),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.vitaminB2Milli.TextField(
            label = stringResource(Res.string.vitamin_b2),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.vitaminB3Milli.TextField(
            label = stringResource(Res.string.vitamin_b3),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.vitaminB5Milli.TextField(
            label = stringResource(Res.string.vitamin_b5),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.vitaminB6Milli.TextField(
            label = stringResource(Res.string.vitamin_b6),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.vitaminB7Micro.TextField(
            label = stringResource(Res.string.vitamin_b7),
            suffix = stringResource(Res.string.unit_microgram_short)
        )

        state.vitaminB9Micro.TextField(
            label = stringResource(Res.string.vitamin_b9),
            suffix = stringResource(Res.string.unit_microgram_short)
        )

        state.vitaminB12Micro.TextField(
            label = stringResource(Res.string.vitamin_b12),
            suffix = stringResource(Res.string.unit_microgram_short)
        )

        state.vitaminCMilli.TextField(
            label = stringResource(Res.string.vitamin_c),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.vitaminDMicro.TextField(
            label = stringResource(Res.string.vitamin_d),
            suffix = stringResource(Res.string.unit_microgram_short)
        )

        state.vitaminEMilli.TextField(
            label = stringResource(Res.string.vitamin_e),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.vitaminKMicro.TextField(
            label = stringResource(Res.string.vitamin_k),
            suffix = stringResource(Res.string.unit_microgram_short)
        )

        Text(
            text = stringResource(Res.string.headline_minerals),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        state.manganeseMilli.TextField(
            label = stringResource(Res.string.mineral_manganese),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.magnesiumMilli.TextField(
            label = stringResource(Res.string.mineral_magnesium),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.potassiumMilli.TextField(
            label = stringResource(Res.string.mineral_potassium),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.calciumMilli.TextField(
            label = stringResource(Res.string.mineral_calcium),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.copperMilli.TextField(
            label = stringResource(Res.string.mineral_copper),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.zincMilli.TextField(
            label = stringResource(Res.string.mineral_zinc),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.sodiumMilli.TextField(
            label = stringResource(Res.string.mineral_sodium),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.ironMilli.TextField(
            label = stringResource(Res.string.mineral_iron),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.phosphorusMilli.TextField(
            label = stringResource(Res.string.mineral_phosphorus),
            suffix = stringResource(Res.string.unit_milligram_short)
        )

        state.seleniumMicro.TextField(
            label = stringResource(Res.string.mineral_selenium),
            suffix = stringResource(Res.string.unit_microgram_short)
        )

        state.iodineMicro.TextField(
            label = stringResource(Res.string.mineral_iodine),
            suffix = stringResource(Res.string.unit_microgram_short),
            imeAction = ImeAction.Done
        )
    }
}

@Composable
internal inline fun <reified T> FormField<T, ProductFormFieldError>.TextField(
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
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
    measurement: Measurement,
    onValueChange: (Measurement) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val possibleValues = remember {
        listOf(
            Measurement.Gram(100f),
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
                Text(measurement.stringResource())
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
