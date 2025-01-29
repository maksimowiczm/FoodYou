package com.maksimowiczm.foodyou.feature.product.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.ui.camera.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.feature.product.ui.res.pluralString
import com.maksimowiczm.foodyou.feature.product.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.component.FullScreenDialog
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun ProductForm(
    modifier: Modifier = Modifier,
    formState: ProductFormState = rememberProductFormState(),
    confirmButton: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ProductForm(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
        formState = formState,
        confirmButton = confirmButton
    )
}

@Composable
private fun ProductForm(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    formState: ProductFormState = rememberProductFormState(),
    confirmButton: @Composable () -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = 300.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FormFieldTextField(
                modifier = Modifier,
                formField = formState.name,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                label = { Text(stringResource(R.string.product_name) + "*") },
                suffix = null
            )
        }

        item {
            FormFieldTextField(
                formField = formState.brand,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                label = { Text(stringResource(R.string.product_brand)) },
                suffix = null
            )
        }

        item {
            FormFieldTextField(
                formField = formState.proteins,
                label = { Text(stringResource(R.string.nutriment_proteins) + "*") },
                suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
            )
        }

        item {
            FormFieldTextField(
                formField = formState.carbohydrates,
                label = { Text(stringResource(R.string.nutriment_carbohydrates) + "*") },
                suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
            )
        }

        item {
            FormFieldTextField(
                formField = formState.fats,
                label = { Text(stringResource(R.string.nutriment_fats) + "*") },
                suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
            )
        }

        item {
            FormFieldTextField(
                formField = formState.calories,
                label = { Text(stringResource(R.string.unit_calories) + "*") },
                suffix = { Text(stringResource(R.string.unit_kcal)) }
            )
        }

        item {
            BarcodeInput(
                barcodeFormField = formState.barcode,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = if (expanded) ImeAction.Next else ImeAction.Done
                )
            )
        }

        if (!expanded) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        modifier = Modifier.widthIn(min = 250.dp),
                        onClick = { onExpandedChange(true) }
                    ) {
                        Text(stringResource(R.string.action_show_additional_fields))
                    }
                }
            }
        }

        if (expanded) {
            item {
                FormFieldTextField(
                    formField = formState.sugars,
                    label = { Text(stringResource(R.string.nutriment_sugars)) },
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
            }

            item {
                FormFieldTextField(
                    formField = formState.saturatedFats,
                    label = { Text(stringResource(R.string.nutriment_saturated_fats)) },
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
            }

            item {
                FormFieldTextField(
                    formField = formState.salt,
                    label = { Text(stringResource(R.string.nutriment_salt)) },
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
            }

            item {
                FormFieldTextField(
                    formField = formState.sodium,
                    label = { Text(stringResource(R.string.nutriment_sodium)) },
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
            }

            item {
                FormFieldTextField(
                    formField = formState.fiber,
                    label = { Text(stringResource(R.string.nutriment_fiber)) },
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) }
                )
            }

            item {
                FormFieldTextField(
                    formField = formState.packageWeight,
                    label = { Text(stringResource(R.string.product_package)) },
                    suffix = { Text(formState.weightUnit.stringResourceShort()) }
                )
            }

            item {
                FormFieldTextField(
                    formField = formState.servingWeight,
                    label = { Text(stringResource(R.string.product_serving)) },
                    suffix = { Text(formState.weightUnit.stringResourceShort()) }
                )
            }

            item {
                WeightUnitDropdownMenu(
                    weightUnit = formState.weightUnit,
                    onWeightUnitChange = formState::onWeightUnitChange
                )
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            // TODO
            //  don't require width
            Box(
                modifier = Modifier.requiredWidth(250.dp),
                contentAlignment = Alignment.Center
            ) {
                confirmButton()
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@Composable
private fun FormFieldTextField(
    formField: FormField<*>,
    label: @Composable () -> Unit,
    suffix: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    )
) {
    var textFieldValue by rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue(formField.toString()))
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            formField.onValueChange(it.text)
        },
        modifier = modifier
            .onFocusChanged {
                if (it.isFocused) {
                    formField.onEnterFocus()
                } else {
                    formField.onExitFocus(textFieldValue.text)
                }
            }
            .widthIn(min = 250.dp, max = 250.dp),
        label = label,
        suffix = suffix,
        isError = formField.error != null,
        supportingText = { formField.error?.let { Text(it.stringResource()) } },
        keyboardOptions = keyboardOptions,
        singleLine = true
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
        OutlinedTextField(
            label = { Text(stringResource(R.string.product_weight_unit)) },
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

@Composable
private fun BarcodeInput(
    barcodeFormField: FormField<String?>,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    )
) {
    var textFieldValue by rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(TextFieldValue(barcodeFormField.toString()))
    }

    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    if (showBarcodeScanner) {
        FullScreenDialog(
            onDismissRequest = { showBarcodeScanner = false }
        ) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    textFieldValue = TextFieldValue(it)
                    barcodeFormField.onValueChange(it)
                    showBarcodeScanner = false
                }
            )
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            barcodeFormField.onValueChange(it.text)
        },
        modifier = modifier
            .onFocusChanged {
                if (it.isFocused) {
                    barcodeFormField.onEnterFocus()
                } else {
                    barcodeFormField.onExitFocus(textFieldValue.text)
                }
            }
            .widthIn(min = 300.dp, max = 300.dp),
        label = { Text(stringResource(R.string.product_barcode)) },
        trailingIcon = {
            IconButton(
                onClick = {
                    barcodeFormField.onEnterFocus()
                    showBarcodeScanner = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qr_code_scanner_24),
                    contentDescription = stringResource(R.string.action_scan_barcode)
                )
            }
        },
        isError = barcodeFormField.error != null,
        supportingText = { barcodeFormField.error?.let { Text(it.stringResource()) } },
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}

@Preview
@Preview(
    device = Devices.TABLET
)
@Composable
private fun ProductFormPreview() {
    FoodYouTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProductForm(
                modifier = Modifier.fillMaxSize(),
                expanded = false,
                onExpandedChange = {}
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}
