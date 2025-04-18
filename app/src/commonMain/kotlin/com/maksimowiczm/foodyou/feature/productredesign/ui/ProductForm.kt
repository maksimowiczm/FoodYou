package com.maksimowiczm.foodyou.feature.productredesign.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ProductForm(
    state: ProductFormState,
    onNameChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
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
    onBarcodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var fabHeight by remember { mutableIntStateOf(0) }
    val sugarsRequester = remember { FocusRequester() }

    val height = LocalDensity.current.run { fabHeight.toDp() }

    Column(
        modifier = modifier.padding(bottom = height),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.headline_general),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        val nameState = rememberInputState(state.name.value) {
            onNameChange(it)
        }
        TextField(
            state = nameState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.product_name)) },
            isError = state.name.isInvalid,
            supportingText = {
                val input = state.name
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    RequiredLabel()
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        val brandState = rememberInputState(state.brand.value) {
            onBrandChange(it)
        }
        TextField(
            state = brandState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.product_brand)) },
            supportingText = { Spacer(Modifier.height(LocalTextStyle.current.toDp())) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        val barcodeState = rememberInputState(state.barcode.value) {
            onBarcodeChange(it)
        }
        TextField(
            state = barcodeState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.product_barcode)) },
            supportingText = { Spacer(Modifier.height(LocalTextStyle.current.toDp())) },
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
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )

        Text(
            text = stringResource(Res.string.headline_macronutrients),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        val proteinsState = rememberInputState(state.proteins.value) {
            onProteinsChange(it)
        }
        TextField(
            state = proteinsState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_proteins)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError =
            state.proteins.isInvalid ||
                state.error == ProductFormError.MacronutrientsExceeds100,
            supportingText = {
                val input = state.proteins
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    RequiredLabel()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        val carbohydratesState = rememberInputState(state.carbohydrates.value) {
            onCarbohydratesChange(it)
        }
        TextField(
            state = carbohydratesState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError =
            state.carbohydrates.isInvalid ||
                state.error == ProductFormError.MacronutrientsExceeds100,
            supportingText = {
                val input = state.carbohydrates
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    RequiredLabel()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        val fatsState = rememberInputState(state.fats.value) {
            onFatsChange(it)
        }
        TextField(
            state = fatsState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_fats)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError =
            state.fats.isInvalid ||
                state.error == ProductFormError.MacronutrientsExceeds100,
            supportingText = {
                val input = state.fats
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    RequiredLabel()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            onKeyboardAction = {
                sugarsRequester.requestFocus()
            }
        )

        TextField(
            value = state.calories?.formatClipZeros() ?: "",
            onValueChange = {},
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            readOnly = true,
            label = { Text(stringResource(Res.string.unit_calories)) },
            supportingText = {
                Text(stringResource(Res.string.neutral_calories_are_calculated))
            },
            isError = state.error == ProductFormError.MacronutrientsExceeds100,
            suffix = { Text(stringResource(Res.string.unit_kcal)) }
        )

        AnimatedVisibility(
            visible = state.error == ProductFormError.MacronutrientsExceeds100,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(
                    Res.string.error_sum_of_macronutrients_cannot_exceed_100g
                ),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Text(
            text = stringResource(Res.string.headline_nutrients),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        val sugarsState = rememberInputState(state.sugars.value) {
            onSugarsChange(it)
        }
        TextField(
            state = sugarsState,
            modifier = Modifier
                .widthIn(min = 300.dp)
                .fillMaxWidth()
                .focusRequester(sugarsRequester),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_sugars)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError = state.sugars.isInvalid,
            supportingText = {
                val input = state.sugars
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    Spacer(Modifier.height(LocalTextStyle.current.toDp()))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        val saturatedFatsState = rememberInputState(state.saturatedFats.value) {
            onSaturatedFatsChange(it)
        }
        TextField(
            state = saturatedFatsState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError = state.saturatedFats.isInvalid,
            supportingText = {
                val input = state.saturatedFats
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    Spacer(Modifier.height(LocalTextStyle.current.toDp()))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        val saltState = rememberInputState(state.salt.value) {
            onSaltChange(it)
        }
        TextField(
            state = saltState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_salt)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError = state.salt.isInvalid,
            supportingText = {
                val input = state.salt
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    Spacer(Modifier.height(LocalTextStyle.current.toDp()))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        val sodiumState = rememberInputState(state.sodium.value) {
            onSodiumChange(it)
        }
        TextField(
            state = sodiumState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_sodium)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError = state.sodium.isInvalid,
            supportingText = {
                val input = state.sodium
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    Spacer(Modifier.height(LocalTextStyle.current.toDp()))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        val fiberState = rememberInputState(state.fiber.value) {
            onFiberChange(it)
        }
        TextField(
            state = fiberState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.nutriment_fiber)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError = state.fiber.isInvalid,
            supportingText = {
                val input = state.fiber
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    Spacer(Modifier.height(LocalTextStyle.current.toDp()))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        Text(
            text = stringResource(Res.string.headline_product_serving_and_packaging),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        val packageWeightState = rememberInputState(state.packageWeight.value) {
            onPackageWeightChange(it)
        }
        TextField(
            state = packageWeightState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.product_package_weight)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError = state.packageWeight.isInvalid,
            supportingText = {
                val input = state.packageWeight
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    Spacer(Modifier.height(LocalTextStyle.current.toDp()))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            )
        )

        val servingWeightState = rememberInputState(state.servingWeight.value) {
            onServingWeightChange(it)
        }
        TextField(
            state = servingWeightState,
            modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
            enabled = enabled,
            label = { Text(stringResource(Res.string.product_serving_weight)) },
            suffix = { Text(stringResource(Res.string.unit_gram_short)) },
            isError = state.servingWeight.isInvalid,
            supportingText = {
                val input = state.servingWeight
                if (input is Input.Invalid) {
                    Text(input.errors.stringResource())
                } else {
                    Spacer(Modifier.height(LocalTextStyle.current.toDp()))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
private fun rememberInputState(
    initialValue: String,
    onValueChange: (String) -> Unit
): TextFieldState {
    val onValueChange by rememberUpdatedState(onValueChange)

    return rememberTextFieldState(initialValue).also {
        LaunchedEffect(it) {
            snapshotFlow { it.text.toString() }
                .drop(1)
                .collectLatest { onValueChange(it) }
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
