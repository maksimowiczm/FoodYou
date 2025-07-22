package com.maksimowiczm.foodyou.feature.food.ui.product

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.form.FormField
import com.maksimowiczm.foodyou.core.ui.unorderedList
import com.maksimowiczm.foodyou.feature.barcodescanner.FullScreenCameraBarcodeScanner
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrderPreference
import com.maksimowiczm.foodyou.feature.food.ui.Icon
import com.maksimowiczm.foodyou.feature.food.ui.stringResource
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProductForm(
    state: ProductFormState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val layoutDirection = LocalLayoutDirection.current
    val horizontalPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection),
        end = contentPadding.calculateStartPadding(layoutDirection)
    )
    val verticalPadding = PaddingValues(
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding()
    )

    val orderPreference = userPreference<NutrientsOrderPreference>()
    val orderState = orderPreference.collectAsStateWithLifecycle(orderPreference.getBlocking())
    val order = if (!orderState.value.containsAll(NutrientsOrder.entries)) {
        NutrientsOrder.defaultOrder
    } else {
        orderState.value
    }

    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }
    FullScreenCameraBarcodeScanner(
        visible = showBarcodeScanner,
        onBarcodeScan = {
            state.barcode.textFieldState.setTextAndPlaceCursorAtEnd(it)
            showBarcodeScanner = false
        },
        onClose = {
            showBarcodeScanner = false
        }
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
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            required = true,
            suffix = null
        )

        state.brand.TextField(
            label = stringResource(Res.string.product_brand),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        BarcodeTextField(
            state = state.barcode,
            onBarcodeScanner = {
                showBarcodeScanner = true
            },
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        state.note.TextField(
            label = stringResource(Res.string.headline_note),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            supportingText = stringResource(Res.string.description_add_note)
        )

        SourcePicker(
            url = state.sourceUrl,
            type = state.sourceType,
            onTypeChange = { state.sourceType = it },
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        MeasurementPicker(
            selected = state.measurement,
            onSelect = { state.measurement = it },
            modifier = Modifier
                .padding(8.dp)
                .padding(horizontalPadding)
                .fillMaxWidth()
        )

        state.packageWeight.TextField(
            label = stringResource(Res.string.product_package_weight),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            required = state.measurement is Measurement.Package
        )

        state.servingWeight.TextField(
            label = stringResource(Res.string.product_serving_weight),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            required = state.measurement is Measurement.Serving
        )

        Text(
            text = stringResource(Res.string.headline_macronutrients),
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        order.forEach { field ->
            when (field) {
                NutrientsOrder.Proteins -> state.proteins.TextField(
                    label = stringResource(Res.string.nutriment_proteins),
                    modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
                    required = true
                )

                NutrientsOrder.Carbohydrates -> state.carbohydrates.TextField(
                    label = stringResource(Res.string.nutriment_carbohydrates),
                    modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
                    required = true
                )

                NutrientsOrder.Fats -> state.fats.TextField(
                    label = stringResource(Res.string.nutriment_fats),
                    modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
                    required = true
                )

                NutrientsOrder.Other,
                NutrientsOrder.Vitamins,
                NutrientsOrder.Minerals -> Unit
            }
        }

        EnergyTextField(
            state = state.energy,
            autoCalculate = state.autoCalculateEnergy,
            onAutoCalculateToggle = { state.autoCalculateEnergy = it },
            modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
        )

        order.forEach { field ->
            when (field) {
                NutrientsOrder.Proteins -> Unit
                NutrientsOrder.Carbohydrates -> Carbohydrates(state, horizontalPadding)
                NutrientsOrder.Fats -> Fats(state, horizontalPadding)
                NutrientsOrder.Other -> Other(state, horizontalPadding)
                NutrientsOrder.Vitamins -> Vitamins(state, horizontalPadding)
                NutrientsOrder.Minerals -> Minerals(state, horizontalPadding)
            }
        }
    }
}

@Composable
private fun ColumnScope.Fats(state: ProductFormState, horizontalPadding: PaddingValues) {
    Text(
        text = stringResource(Res.string.nutriment_fats),
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontalPadding)
            .fillMaxWidth(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    state.saturatedFats.TextField(
        label = stringResource(Res.string.nutriment_saturated_fats),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
    )

    state.transFats.TextField(
        label = stringResource(Res.string.nutriment_trans_fats),
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
}

@Composable
private fun ColumnScope.Carbohydrates(state: ProductFormState, horizontalPadding: PaddingValues) {
    Text(
        text = stringResource(Res.string.nutriment_carbohydrates),
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontalPadding)
            .fillMaxWidth(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    state.sugars.TextField(
        label = stringResource(Res.string.nutriment_sugars),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
    )

    state.addedSugars.TextField(
        label = stringResource(Res.string.nutriment_added_sugars),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
    )

    state.dietaryFiber.TextField(
        label = stringResource(Res.string.nutriment_fiber),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
    )

    state.solubleFiber.TextField(
        label = stringResource(Res.string.nutriment_soluble_fiber),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
    )

    state.insolubleFiber.TextField(
        label = stringResource(Res.string.nutriment_insoluble_fiber),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
    )
}

@Composable
private fun ColumnScope.Other(state: ProductFormState, horizontalPadding: PaddingValues) {
    Text(
        text = stringResource(Res.string.headline_other),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    state.salt.TextField(
        label = stringResource(Res.string.nutriment_salt),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth()
    )

    state.cholesterolMilli.TextField(
        label = stringResource(Res.string.nutriment_cholesterol),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.caffeineMilli.TextField(
        label = stringResource(Res.string.nutriment_caffeine),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )
}

@Composable
private fun ColumnScope.Vitamins(state: ProductFormState, horizontalPadding: PaddingValues) {
    Text(
        text = stringResource(Res.string.headline_vitamins),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    state.vitaminAMicro.TextField(
        label = stringResource(Res.string.vitamin_a),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )

    state.vitaminB1Milli.TextField(
        label = stringResource(Res.string.vitamin_b1),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.vitaminB2Milli.TextField(
        label = stringResource(Res.string.vitamin_b2),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.vitaminB3Milli.TextField(
        label = stringResource(Res.string.vitamin_b3),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.vitaminB5Milli.TextField(
        label = stringResource(Res.string.vitamin_b5),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.vitaminB6Milli.TextField(
        label = stringResource(Res.string.vitamin_b6),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.vitaminB7Micro.TextField(
        label = stringResource(Res.string.vitamin_b7),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )

    state.vitaminB9Micro.TextField(
        label = stringResource(Res.string.vitamin_b9),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )

    state.vitaminB12Micro.TextField(
        label = stringResource(Res.string.vitamin_b12),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )

    state.vitaminCMilli.TextField(
        label = stringResource(Res.string.vitamin_c),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.vitaminDMicro.TextField(
        label = stringResource(Res.string.vitamin_d),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )

    state.vitaminEMilli.TextField(
        label = stringResource(Res.string.vitamin_e),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.vitaminKMicro.TextField(
        label = stringResource(Res.string.vitamin_k),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )
}

@Composable
private fun ColumnScope.Minerals(state: ProductFormState, horizontalPadding: PaddingValues) {
    Text(
        text = stringResource(Res.string.headline_minerals),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    state.manganeseMilli.TextField(
        label = stringResource(Res.string.mineral_manganese),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.magnesiumMilli.TextField(
        label = stringResource(Res.string.mineral_magnesium),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.potassiumMilli.TextField(
        label = stringResource(Res.string.mineral_potassium),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.calciumMilli.TextField(
        label = stringResource(Res.string.mineral_calcium),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.copperMilli.TextField(
        label = stringResource(Res.string.mineral_copper),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.zincMilli.TextField(
        label = stringResource(Res.string.mineral_zinc),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.sodiumMilli.TextField(
        label = stringResource(Res.string.mineral_sodium),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.ironMilli.TextField(
        label = stringResource(Res.string.mineral_iron),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.phosphorusMilli.TextField(
        label = stringResource(Res.string.mineral_phosphorus),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_milligram_short)
    )

    state.seleniumMicro.TextField(
        label = stringResource(Res.string.mineral_selenium),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )

    state.iodineMicro.TextField(
        label = stringResource(Res.string.mineral_iodine),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short)
    )

    state.chromiumMicro.TextField(
        label = stringResource(Res.string.mineral_chromium),
        modifier = Modifier.padding(horizontalPadding).fillMaxWidth(),
        suffix = stringResource(Res.string.unit_microgram_short),
        imeAction = ImeAction.Done
    )
}

@Composable
private inline fun <reified T> FormField<T, ProductFormFieldError>.TextField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    suffix: String? = stringResource(Res.string.unit_gram_short)
) {
    OutlinedTextField(
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
                imeAction = imeAction
            )
        } else {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            )
        }
    )
}

@Composable
private inline fun <reified T> FormField<T, Nothing>.TextField(
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    supportingText: String? = null
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
        label = { Text(label) },
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = if (T::class == Float::class) {
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = imeAction
            )
        } else {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            )
        }
    )
}

@Composable
private fun BarcodeTextField(
    state: FormField<String?, Nothing>,
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        state = state.textFieldState,
        modifier = modifier,
        label = { Text(stringResource(Res.string.product_barcode)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        trailingIcon = {
            FilledTonalIconButton(
                onClick = onBarcodeScanner
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_barcode_scanner),
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MeasurementPicker(
    selected: Measurement,
    onSelect: (Measurement) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val possibleValues = remember {
        listOf(
            Measurement.Gram(100f),
            Measurement.Milliliter(100f),
            Measurement.Serving(1f),
            Measurement.Package(1f)
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.headline_values_per),
            style = MaterialTheme.typography.bodyLarge
        )

        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = { expanded = !expanded }
                ) {
                    Text(
                        text = selected.stringResource()
                    )
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    checked = expanded,
                    onCheckedChange = { expanded = it }
                ) {
                    val rotation by animateFloatAsState(
                        targetValue = if (expanded) 180f else 0f
                    )

                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        modifier =
                            Modifier.size(SplitButtonDefaults.TrailingIconSize).graphicsLayer {
                                this.rotationZ = rotation
                            },
                        contentDescription = null
                    )
                }

                // Had to put it here because it must be aligned to the trailing button
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    possibleValues.forEach {
                        DropdownMenuItem(
                            text = { Text(it.stringResource()) },
                            onClick = {
                                expanded = false
                                onSelect(it)
                            }
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun EnergyTextField(
    state: FormField<Float?, ProductFormFieldError>,
    autoCalculate: Boolean,
    onAutoCalculateToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            state = state.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.unit_energy)) },
            supportingText = {
                val error = state.error
                if (error != null) {
                    Text(error.stringResource())
                } else {
                    Text(stringResource(Res.string.neutral_required))
                }
            },
            trailingIcon = {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = if (autoCalculate) {
                                    "Auto-calculate energy"
                                } else {
                                    "Manual energy input"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    state = rememberTooltipState(
                        isPersistent = true
                    )
                ) {
                    IconButton(
                        onClick = { onAutoCalculateToggle(!autoCalculate) }
                    ) {
                        if (autoCalculate) {
                            Icon(
                                imageVector = Icons.Outlined.Calculate,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Keyboard,
                                contentDescription = null
                            )
                        }
                    }
                }
            },
            isError = state.error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            suffix = { Text(stringResource(Res.string.unit_kcal)) }
        )
        Text(
            text = stringResource(Res.string.description_calories_are_calculated),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = unorderedList(
                stringResource(
                    Res.string.x_kcal_per_g,
                    stringResource(Res.string.nutriment_proteins),
                    4
                ),
                stringResource(
                    Res.string.x_kcal_per_g,
                    stringResource(Res.string.nutriment_carbohydrates),
                    4
                ),
                stringResource(
                    Res.string.x_kcal_per_g,
                    stringResource(Res.string.nutriment_fats),
                    9
                )
            ),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SourcePicker(
    url: FormField<String?, Nothing>,
    type: FoodSource.Type,
    onTypeChange: (FoodSource.Type) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val dropdownMenu = @Composable {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FoodSource.Type.entries.forEach {
                DropdownMenuItem(
                    leadingIcon = { it.Icon() },
                    text = { Text(it.stringResource()) },
                    onClick = {
                        expanded = false
                        onTypeChange(it)
                    }
                )
            }
        }
    }

    OutlinedTextField(
        state = url.textFieldState,
        modifier = modifier,
        label = { Text(stringResource(Res.string.headline_source)) },
        supportingText = {
            Text(stringResource(Res.string.description_food_source))
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        trailingIcon = {
            dropdownMenu()
            FilledTonalIconButton(
                onClick = { expanded = true }
            ) {
                type.Icon()
            }
        }
    )
}
