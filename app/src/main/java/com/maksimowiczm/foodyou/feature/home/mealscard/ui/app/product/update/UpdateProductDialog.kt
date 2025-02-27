package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.update

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.WeightUnit
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner.CameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.GlobalError
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.MyError
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.ProductFormState
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.rememberProductFormState
import com.maksimowiczm.foodyou.ui.component.FullScreenDialog
import com.maksimowiczm.foodyou.ui.ext.plus
import com.maksimowiczm.foodyou.ui.ext.toDp
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.preview.BooleanPreviewParameter
import com.maksimowiczm.foodyou.ui.preview.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.res.pluralString
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun UpdateProductDialog(
    onClose: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateProductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UpdateProductDialog(
        state = uiState,
        onNavigateBack = onClose,
        onSuccess = onSuccess,
        onUpdateProduct = viewModel::updateProduct,
        modifier = modifier
    )
}

@Composable
private fun UpdateProductDialog(
    state: UpdateProductState,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    onUpdateProduct: (ProductFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdditionalFields by rememberSaveable { mutableStateOf(false) }

    val latestOnSuccess by rememberUpdatedState(onSuccess)
    LaunchedEffect(state, onSuccess) {
        when (state) {
            UpdateProductState.Loading,
            is UpdateProductState.ProductReady,
            is UpdateProductState.UpdatingProduct -> Unit

            is UpdateProductState.ProductUpdated -> latestOnSuccess()
        }
    }

    when (state) {
        UpdateProductState.Loading -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        is UpdateProductState.WithProduct -> UpdateProductDialog(
            form = rememberProductFormState(
                product = state.product
            ),
            onClose = onNavigateBack,
            onUpdate = onUpdateProduct,
            expanded = showAdditionalFields,
            onExpandedChange = { showAdditionalFields = it },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateProductDialog(
    form: ProductFormState,
    onClose: () -> Unit,
    onUpdate: (ProductFormState) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(WindowInsets.ime)

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onConfirm = onClose
        )
    }

    val handleClose = {
        if (form.isDirty) {
            showDiscardDialog = true
        } else {
            onClose()
        }
    }

    BackHandler(
        enabled = form.isDirty
    ) {
        showDiscardDialog = true
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = handleClose
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.action_close)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.headline_edit_product)
                    )
                },
                actions = {
                    TextButton(
                        onClick = { onUpdate(form) },
                        enabled = form.isValid
                    ) {
                        Text(
                            text = stringResource(R.string.action_save)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = contentWindowInsets
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues + PaddingValues(16.dp)
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = stringResource(R.string.headline_general),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                Column {
                    form.brand.TextField(
                        label = {
                            Text(
                                text = stringResource(R.string.product_brand)
                            )
                        },
                        suffix = null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                Column {
                    form.name.TextField(
                        label = {
                            Text(
                                text = stringResource(R.string.product_name) + "*"
                            )
                        },
                        suffix = null,
                        supportingText = {
                            Text(
                                text = "*" + stringResource(R.string.neutral_required)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                Column {
                    BarcodeInput(
                        barcodeFormField = form.barcode,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(
                            R.string.neutral_all_values_per_x,
                            "100 ${form.weightUnit.pluralString(100)}"
                        )
                    )
                }
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = stringResource(R.string.headline_macronutrients),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                Column {
                    form.proteins.TextField(
                        label = {
                            Text(
                                text = stringResource(R.string.nutriment_proteins) + "*"
                            )
                        },
                        suffix = {
                            Text(
                                text = WeightUnit.Gram.stringResourceShort()
                            )
                        },
                        supportingText = {
                            Text(
                                text = "*" + stringResource(R.string.neutral_required)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        errorOverride =
                        form.globalError == GlobalError.MacronutrientsSumExceeds100
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                Column {
                    form.carbohydrates.TextField(
                        label = {
                            Text(
                                text = stringResource(R.string.nutriment_carbohydrates) + "*"
                            )
                        },
                        suffix = {
                            Text(
                                text = WeightUnit.Gram.stringResourceShort()
                            )
                        },
                        supportingText = {
                            Text(
                                text = "*" + stringResource(R.string.neutral_required)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        errorOverride =
                        form.globalError == GlobalError.MacronutrientsSumExceeds100
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                Column {
                    form.fats.TextField(
                        label = {
                            Text(
                                text = stringResource(R.string.nutriment_fats) + "*"
                            )
                        },
                        suffix = {
                            Text(
                                text = WeightUnit.Gram.stringResourceShort()
                            )
                        },
                        supportingText = {
                            Text(
                                text = "*" + stringResource(R.string.neutral_required)
                            )
                        },
                        keyboardOptions = if (expanded) {
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        } else {
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        errorOverride =
                        form.globalError == GlobalError.MacronutrientsSumExceeds100
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item {
                Column {
                    form.calories.TextField(
                        label = {
                            Text(
                                text = stringResource(R.string.unit_calories)
                            )
                        },
                        suffix = {
                            Text(
                                text = stringResource(R.string.unit_kcal)
                            )
                        },
                        supportingText = {
                            Text(
                                text = stringResource(R.string.neutral_calories_are_calculated),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                AnimatedVisibility(
                    visible = form.globalError == GlobalError.MacronutrientsSumExceeds100
                ) {
                    Text(
                        text = stringResource(
                            R.string.error_sum_of_macronutrients_cannot_exceed_100g
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (expanded) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Text(
                        text = stringResource(R.string.headline_nutrients),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                item {
                    Column {
                        form.sugars.TextField(
                            label = {
                                Text(
                                    text = stringResource(R.string.nutriment_sugars)
                                )
                            },
                            suffix = {
                                Text(
                                    text = WeightUnit.Gram.stringResourceShort()
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Column {
                        form.saturatedFats.TextField(
                            label = {
                                Text(
                                    text = stringResource(R.string.nutriment_saturated_fats)
                                )
                            },
                            suffix = {
                                Text(
                                    text = WeightUnit.Gram.stringResourceShort()
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Column {
                        form.salt.TextField(
                            label = {
                                Text(
                                    text = stringResource(R.string.nutriment_salt)
                                )
                            },
                            suffix = {
                                Text(
                                    text = WeightUnit.Gram.stringResourceShort()
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Column {
                        form.sodium.TextField(
                            label = {
                                Text(
                                    text = stringResource(R.string.nutriment_sodium)
                                )
                            },
                            suffix = {
                                Text(
                                    text = WeightUnit.Gram.stringResourceShort()
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Column {
                        form.fiber.TextField(
                            label = {
                                Text(
                                    text = stringResource(R.string.nutriment_fiber)
                                )
                            },
                            suffix = {
                                Text(
                                    text = WeightUnit.Gram.stringResourceShort()
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Text(
                        text = stringResource(R.string.headline_product_serving_and_packaging),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                item {
                    Column {
                        form.packageWeight.TextField(
                            label = {
                                Text(
                                    text = stringResource(R.string.product_package_weight)
                                )
                            },
                            suffix = {
                                Text(
                                    text = form.weightUnit.stringResourceShort()
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Column {
                        form.servingWeight.TextField(
                            label = {
                                Text(
                                    text = stringResource(R.string.product_serving_weight)
                                )
                            },
                            suffix = {
                                Text(
                                    text = form.weightUnit.stringResourceShort()
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    Column {
                        WeightUnitDropdownMenu(
                            weightUnit = form.weightUnit,
                            onWeightUnitChange = { form.weightUnit = it }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
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
                            onClick = { onExpandedChange(true) }
                        ) {
                            Text(stringResource(R.string.action_show_remaining_fields))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun <T> FormFieldWithTextFieldValue<T, MyError>.TextField(
    label: @Composable () -> Unit,
    suffix: (@Composable (() -> Unit))?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: (@Composable (() -> Unit))? = null,
    supportingText: @Composable (() -> Unit) = {
        Spacer(Modifier.height(LocalTextStyle.current.toDp()))
    },
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    ),
    keyboardActions: KeyboardActions = KeyboardActions(),
    errorOverride: Boolean = false
) {
    @Suppress("NAME_SHADOWING")
    val trailingIcon: @Composable (() -> Unit)? = if (error != null) {
        {
            Icon(
                painter = painterResource(R.drawable.ic_error_24_fill),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    } else {
        trailingIcon
    }

    @Suppress("NAME_SHADOWING")
    val supportingText: @Composable () -> Unit = if (error != null) {
        {
            Text(
                text = error.stringResource(),
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        supportingText
    }

    TextField(
        value = textFieldValue,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        label = label,
        suffix = suffix,
        trailingIcon = trailingIcon,
        isError = error != null || errorOverride,
        supportingText = supportingText,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = 1,
        enabled = enabled
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
    barcodeFormField: FormFieldWithTextFieldValue<String?, MyError>,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next
    )
) {
    var showBarcodeScanner by rememberSaveable { mutableStateOf(false) }

    if (showBarcodeScanner) {
        FullScreenDialog(
            onDismissRequest = { showBarcodeScanner = false }
        ) {
            CameraBarcodeScannerScreen(
                onBarcodeScan = {
                    barcodeFormField.onRawValueChange(it)
                    showBarcodeScanner = false
                },
                onClose = { showBarcodeScanner = false }
            )
        }
    }

    barcodeFormField.TextField(
        label = { Text(stringResource(R.string.product_barcode)) },
        suffix = null,
        trailingIcon = {
            IconButton(
                onClick = {
                    showBarcodeScanner = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qr_code_scanner_24),
                    contentDescription = stringResource(R.string.action_scan_barcode)
                )
            }
        },
        modifier = modifier.widthIn(min = 300.dp, max = 300.dp),
        keyboardOptions = keyboardOptions
    )
}

@Composable
private fun DiscardDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(stringResource(R.string.action_discard))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        text = {
            Text(stringResource(R.string.question_discard_changes))
        }
    )
}

@Preview(
    device = "spec:width=400dp,height=1400dp"
)
@Preview(
    device = Devices.TABLET
)
@Composable
private fun CreateProductScreenPreview(
    @PreviewParameter(BooleanPreviewParameter::class) expanded: Boolean
) {
    FoodYouTheme {
        UpdateProductDialog(
            form = rememberProductFormState(
                product = ProductPreviewParameterProvider().values.first()
            ),
            onClose = {},
            onUpdate = {},
            expanded = expanded,
            onExpandedChange = {}
        )
    }
}
