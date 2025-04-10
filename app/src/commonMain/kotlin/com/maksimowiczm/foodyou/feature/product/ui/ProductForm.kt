package com.maksimowiczm.foodyou.feature.product.ui

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pro.respawn.kmmutils.inputforms.Input

@Composable
internal fun ProductForm(
    contentPadding: PaddingValues,
    state: ProductFormState,
    onNameChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onBarcodeScanner: () -> Unit,
    onBarcodeChange: (String) -> Unit,
    onProteinsChange: (String) -> Unit,
    onCarbohydratesChange: (String) -> Unit,
    onFatsChange: (String) -> Unit,
    onSugarsChange: (String) -> Unit,
    onSaturatedFatsChange: (String) -> Unit,
    onSaltChange: (String) -> Unit,
    onSodiumChange: (String) -> Unit,
    onFiberChange: (String) -> Unit,
    onPackageWeightChange: (String) -> Unit,
    onServingWeightChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val expandedFocusRequester = remember { FocusRequester() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    val topOffset = LocalDensity.current.run { contentPadding.calculateTopPadding().toPx() }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = modifier,
        state = gridState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Text(
                text = stringResource(Res.string.headline_general),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        item {
            val input = state.name

            TextField(
                value = input.value,
                onValueChange = onNameChange,
                label = { Text(stringResource(Res.string.product_name)) },
                supportingText = {
                    if (input is Input.Invalid) {
                        Text(
                            text = input.errors.stringResource(),
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        RequiredLabel()
                    }
                },
                isError = input is Input.Invalid,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        item {
            val input = state.brand

            TextField(
                value = input.value,
                onValueChange = onBrandChange,
                label = { Text(stringResource(Res.string.product_brand)) },
                supportingText = {
                    if (input is Input.Invalid) {
                        Text(
                            text = input.errors.stringResource(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = input is Input.Invalid,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        item {
            val input = state.barcode

            TextField(
                value = input.value,
                onValueChange = onBarcodeChange,
                label = { Text(stringResource(Res.string.product_barcode)) },
                supportingText = {
                    if (input is Input.Invalid) {
                        Text(
                            text = input.errors.stringResource(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = onBarcodeScanner
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(Res.string.action_scan_barcode)
                        )
                    }
                },
                isError = input is Input.Invalid,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
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
                        Res.string.neutral_all_values_per_x,
                        "100 " + stringResource(Res.string.unit_gram_short)
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
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        item {
            val input = state.proteins

            TextField(
                value = input.value,
                onValueChange = { onProteinsChange(it) },
                label = { Text(stringResource(Res.string.nutriment_proteins)) },
                supportingText = {
                    when {
                        input is Input.Invalid -> Text(
                            text = input.errors.stringResource(),
                            color = MaterialTheme.colorScheme.error
                        )

                        else -> RequiredLabel()
                    }
                },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError =
                input is Input.Invalid ||
                    state.error is ProductFormError.MacronutrientsSumExceeds100,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        item {
            val input = state.carbohydrates

            TextField(
                value = input.value,
                onValueChange = { onCarbohydratesChange(it) },
                label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                supportingText = {
                    when {
                        input is Input.Invalid -> Text(
                            text = input.errors.stringResource(),
                            color = MaterialTheme.colorScheme.error
                        )

                        else -> RequiredLabel()
                    }
                },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError =
                input is Input.Invalid ||
                    state.error is ProductFormError.MacronutrientsSumExceeds100,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }

        item {
            val input = state.fats

            TextField(
                value = input.value,
                onValueChange = { onFatsChange(it) },
                label = { Text(stringResource(Res.string.nutriment_fats)) },
                supportingText = {
                    when {
                        input is Input.Invalid -> Text(
                            text = input.errors.stringResource(),
                            color = MaterialTheme.colorScheme.error
                        )

                        else -> RequiredLabel()
                    }
                },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError =
                input is Input.Invalid ||
                    state.error is ProductFormError.MacronutrientsSumExceeds100,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (expanded) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        runCatching {
                            coroutineScope.launch {
                                // Scroll to sugars field
                                gridState.animateScrollToItem(13, -topOffset.roundToInt())
                                expandedFocusRequester.requestFocus()
                            }
                        }
                    }
                ),
                singleLine = true
            )
        }

        item {
            val value = state.calories?.formatClipZeros() ?: ""

            TextField(
                value = value,
                onValueChange = {},
                supportingText = {
                    Text(stringResource(Res.string.neutral_calories_are_calculated))
                },
                label = { Text(stringResource(Res.string.unit_calories)) },
                suffix = { Text(stringResource(Res.string.unit_kcal)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                readOnly = true
            )
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            AnimatedVisibility(
                visible = state.error is ProductFormError.MacronutrientsSumExceeds100
            ) {
                Text(
                    text = stringResource(
                        Res.string.error_sum_of_macronutrients_cannot_exceed_100g
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
                    text = stringResource(Res.string.headline_nutrients),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // 13
            item {
                val input = state.sugars

                TextField(
                    value = input.value,
                    onValueChange = { onSugarsChange(it) },
                    modifier = Modifier.focusRequester(expandedFocusRequester),
                    label = { Text(stringResource(Res.string.nutriment_sugars)) },
                    supportingText = {
                        if (input is Input.Invalid) {
                            Text(
                                text = input.errors.stringResource(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = input is Input.Invalid,
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            item {
                val input = state.saturatedFats

                TextField(
                    value = input.value,
                    onValueChange = { onSaturatedFatsChange(it) },
                    label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
                    supportingText = {
                        if (input is Input.Invalid) {
                            Text(
                                text = input.errors.stringResource(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = input is Input.Invalid,
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            item {
                val input = state.salt

                TextField(
                    value = input.value,
                    onValueChange = { onSaltChange(it) },
                    label = { Text(stringResource(Res.string.nutriment_salt)) },
                    supportingText = {
                        if (input is Input.Invalid) {
                            Text(
                                text = input.errors.stringResource(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = input is Input.Invalid,
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            item {
                val input = state.sodium

                TextField(
                    value = input.value,
                    onValueChange = { onSodiumChange(it) },
                    label = { Text(stringResource(Res.string.nutriment_sodium)) },
                    supportingText = {
                        if (input is Input.Invalid) {
                            Text(
                                text = input.errors.stringResource(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = input is Input.Invalid,
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            item {
                val input = state.fiber

                TextField(
                    value = input.value,
                    onValueChange = { onFiberChange(it) },
                    label = { Text(stringResource(Res.string.nutriment_fiber)) },
                    supportingText = {
                        if (input is Input.Invalid) {
                            Text(
                                text = input.errors.stringResource(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = input is Input.Invalid,
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = stringResource(Res.string.headline_product_serving_and_packaging),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            item {
                val input = state.packageWeight

                TextField(
                    value = input.value,
                    onValueChange = { onPackageWeightChange(it) },
                    label = { Text(stringResource(Res.string.product_package_weight)) },
                    supportingText = {
                        if (input is Input.Invalid) {
                            Text(
                                text = input.errors.stringResource(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = input is Input.Invalid,
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
            }

            item {
                val input = state.servingWeight

                TextField(
                    value = input.value,
                    onValueChange = { onServingWeightChange(it) },
                    label = { Text(stringResource(Res.string.product_serving_weight)) },
                    supportingText = {
                        if (input is Input.Invalid) {
                            Text(
                                text = input.errors.stringResource(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = input is Input.Invalid,
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
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
private fun RequiredLabel(modifier: Modifier = Modifier) {
    Text(
        text = "* " + stringResource(Res.string.neutral_required),
        modifier = modifier
    )
}
