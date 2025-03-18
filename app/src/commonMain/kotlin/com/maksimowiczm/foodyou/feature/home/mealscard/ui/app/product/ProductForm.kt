package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.data.model.WeightUnit
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.ui.component.FullScreenDialog
import com.maksimowiczm.foodyou.ui.ext.plus
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.preview.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.res.pluralString
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductForm(
    state: ProductFormState,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val expandedFocusRequester = remember { FocusRequester() }

    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }
    AnimatedVisibility(
        visible = showBarcodeScanner
    ) {
        FullScreenDialog(
            onDismissRequest = { showBarcodeScanner = false }
        ) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    state.barcode.onRawValueChange(it)
                    showBarcodeScanner = false
                },
                onClose = { showBarcodeScanner = false }
            )
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = modifier,
        contentPadding = paddingValues + PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Text(
                text = stringResource(Res.string.headline_general),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        item {
            state.name.TextFieldString(
                label = { Text(stringResource(Res.string.product_name)) },
                modifier = Modifier.padding(bottom = 4.dp),
                supportingText = { Text(requiredString()) }
            )
        }

        item {
            state.brand.TextFieldString(
                label = { Text(stringResource(Res.string.product_brand)) },
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        item {
            state.barcode.TextFieldString(
                label = { Text(stringResource(Res.string.product_barcode)) },
                modifier = Modifier.padding(bottom = 4.dp),
                trailingIcon = {
                    IconButton(
                        onClick = { showBarcodeScanner = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(Res.string.action_scan_barcode)
                        )
                    }
                }
            )
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(
                        Res.string.neutral_all_values_per_x,
                        "100 ${WeightUnit.Gram.pluralString(100)}"
                    )
                )
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Text(
                text = stringResource(Res.string.headline_macronutrients),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        item {
            state.proteins.TextFieldNumber(
                label = { Text(stringResource(Res.string.nutriment_proteins)) },
                modifier = Modifier.padding(bottom = 4.dp),
                supportingText = { Text(requiredString()) },
                overrideError = state.globalError == GlobalError.MacronutrientsSumExceeds100
            )
        }

        item {
            state.carbohydrates.TextFieldNumber(
                label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                modifier = Modifier.padding(bottom = 4.dp),
                supportingText = { Text(requiredString()) },
                overrideError = state.globalError == GlobalError.MacronutrientsSumExceeds100
            )
        }

        item {
            state.fats.TextFieldNumber(
                label = { Text(stringResource(Res.string.nutriment_fats)) },
                modifier = Modifier.padding(bottom = 4.dp),
                supportingText = { Text(requiredString()) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (expanded) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = { expandedFocusRequester.requestFocus() }
                ),
                overrideError = state.globalError == GlobalError.MacronutrientsSumExceeds100
            )
        }

        item {
            state.calories.TextField(
                label = { Text(stringResource(Res.string.unit_calories)) },
                keyboardOptions = KeyboardOptions.Default,
                modifier = Modifier.padding(bottom = 4.dp),
                supportingText = {
                    Text(stringResource(Res.string.neutral_calories_are_calculated))
                },
                suffix = { Text(stringResource(Res.string.unit_kcal)) },
                readOnly = true
            )
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            AnimatedVisibility(
                visible = state.globalError == GlobalError.MacronutrientsSumExceeds100
            ) {
                Text(
                    text = stringResource(
                        Res.string.error_sum_of_macronutrients_cannot_exceed_100g
                    ),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Spacer(Modifier.height(14.dp))
        }

        // More fields
        if (expanded) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = stringResource(Res.string.headline_nutrients),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                state.sugars.TextFieldNumber(
                    label = { Text(stringResource(Res.string.nutriment_sugars)) },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .focusRequester(expandedFocusRequester)
                )
            }

            item {
                state.saturatedFats.TextFieldNumber(
                    label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            item {
                state.salt.TextFieldNumber(
                    label = { Text(stringResource(Res.string.nutriment_salt)) },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            item {
                state.sodium.TextFieldNumber(
                    label = { Text(stringResource(Res.string.nutriment_sodium)) },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            item {
                state.fiber.TextFieldNumber(
                    label = { Text(stringResource(Res.string.nutriment_fiber)) },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = stringResource(Res.string.headline_product_serving_and_packaging),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                state.packageWeight.TextFieldNumber(
                    label = { Text(stringResource(Res.string.product_package_weight)) },
                    modifier = Modifier.padding(bottom = 4.dp),
                    suffix = { Text(state.weightUnit.stringResourceShort()) }
                )
            }

            item {
                state.servingWeight.TextFieldNumber(
                    label = { Text(stringResource(Res.string.product_serving_weight)) },
                    modifier = Modifier.padding(bottom = 4.dp),
                    suffix = { Text(state.weightUnit.stringResourceShort()) }
                )
            }

            item {
                WeightUnitDropdownMenu(
                    weightUnit = state.weightUnit,
                    onWeightUnitChange = { state.weightUnit = it }
                )
            }
        } else {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        modifier = Modifier.widthIn(min = 250.dp),
                        onClick = { expanded = true }
                    ) {
                        Text(stringResource(Res.string.action_show_remaining_fields))
                    }
                }
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun requiredString(): String = "* " + stringResource(Res.string.neutral_required)

@Composable
private fun <T> FormFieldWithTextFieldValue<T, ProductFormError>.TextField(
    label: @Composable () -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    supportingText: @Composable () -> Unit = {
        Spacer(Modifier.height(LocalTextStyle.current.toDp()))
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    overrideError: Boolean = false
) {
    // https://issuetracker.google.com/issues/294102838?pli=1
    // Some keyboards will delete whitespaces between words 💀
    TextField(
        value = textFieldValue,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        label = label,
        isError = error != null || overrideError,
        supportingText = {
            if (error == null) {
                supportingText.invoke()
            } else {
                Text(error.stringResource())
            }
        },
        trailingIcon = trailingIcon,
        suffix = suffix,
        maxLines = 1,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

@Composable
private fun <T : Number?> FormFieldWithTextFieldValue<T, ProductFormError>.TextFieldNumber(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: @Composable () -> Unit = {
        Spacer(Modifier.height(LocalTextStyle.current.toDp()))
    },
    suffix: @Composable (() -> Unit)? = { Text(stringResource(Res.string.unit_gram_short)) },
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    overrideError: Boolean = false
) {
    this.TextField(
        label = label,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        supportingText = supportingText,
        suffix = suffix,
        keyboardActions = keyboardActions,
        overrideError = overrideError
    )
}

@Composable
private fun FormFieldWithTextFieldValue<String?, ProductFormError>.TextFieldString(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: @Composable () -> Unit = {
        Spacer(Modifier.height(LocalTextStyle.current.toDp()))
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
) {
    this.TextField(
        label = label,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        supportingText = supportingText,
        trailingIcon = trailingIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightUnitDropdownMenu(
    weightUnit: WeightUnit,
    onWeightUnitChange: (WeightUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            label = { Text(stringResource(Res.string.product_weight_unit)) },
            value = weightUnit.pluralString(1),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WeightUnit.entries.mapIndexed { i, it ->
                DropdownMenuItem(
                    text = { Text(it.pluralString(1)) },
                    onClick = {
                        onWeightUnitChange(it)
                        expanded = false
                    }
                )

                if (i < WeightUnit.entries.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview
@Composable
private fun NullProductFormPreview() {
    FoodYouTheme {
        ProductForm(
            state = rememberProductFormState(null)
        )
    }
}

@Preview
@Composable
private fun ProductFormPreview() {
    FoodYouTheme {
        ProductForm(
            state = rememberProductFormState(ProductPreviewParameterProvider().values.first())
        )
    }
}
