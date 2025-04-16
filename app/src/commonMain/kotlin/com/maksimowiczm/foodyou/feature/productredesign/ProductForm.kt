package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ProductForm(
    state: ProductFormState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    contentPadding: PaddingValues,
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
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current

    var fabHeight by remember { mutableIntStateOf(0) }
    val sugarsRequester = remember { FocusRequester() }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.animateFloatingActionButton(
                    visible = !animatedVisibilityScope.transition.isRunning,
                    alignment = Alignment.BottomEnd
                ).onSizeChanged {
                    fabHeight = it.height
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(Res.string.action_create)
                )
            }
        }
    ) {
        FlowRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection)
                )
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(contentPadding.calculateTopPadding()).fillMaxWidth())

            Text(
                text = stringResource(Res.string.headline_general),
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            val name by remember(state) {
                derivedStateOf { state.name.value }
            }
            TextField(
                value = TextFieldValue(name, TextRange(name.length)),
                onValueChange = { onNameChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_name)) },
                isError = !state.name.isValid,
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

            val brand by remember(state) {
                derivedStateOf { state.brand.value }
            }
            TextField(
                value = TextFieldValue(brand, TextRange(brand.length)),
                onValueChange = { onBrandChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_brand)) },
                supportingText = { Spacer(Modifier.height(LocalTextStyle.current.toDp())) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            TextField(
                value = TextFieldValue(state.barcode.value, TextRange(state.barcode.value.length)),
                onValueChange = { onBarcodeChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_barcode)) },
                supportingText = { Spacer(Modifier.height(LocalTextStyle.current.toDp())) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            Text(
                text = stringResource(Res.string.headline_macronutrients),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            val proteins by remember(state) {
                derivedStateOf { state.proteins.value }
            }
            TextField(
                value = TextFieldValue(proteins, TextRange(proteins.length)),
                onValueChange = { onProteinsChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_proteins)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.proteins.isValid,
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

            val carbohydrates by remember(state) {
                derivedStateOf { state.carbohydrates.value }
            }
            TextField(
                value = TextFieldValue(carbohydrates, TextRange(carbohydrates.length)),
                onValueChange = { onCarbohydratesChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.carbohydrates.isValid,
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

            val fats by remember(state) {
                derivedStateOf { state.fats.value }
            }
            TextField(
                value = TextFieldValue(fats, TextRange(fats.length)),
                onValueChange = { onFatsChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_fats)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.fats.isValid,
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
                keyboardActions = KeyboardActions {
                    sugarsRequester.requestFocus()
                }
            )

            val calories by remember(state) {
                derivedStateOf {
                    state.calories?.formatClipZeros() ?: ""
                }
            }
            TextField(
                value = calories,
                onValueChange = {},
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.unit_calories)) },
                supportingText = {
                    Text(stringResource(Res.string.neutral_calories_are_calculated))
                },
                suffix = { Text(stringResource(Res.string.unit_kcal)) },
                readOnly = true
            )

            Text(
                text = stringResource(Res.string.headline_nutrients),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            val sugars by remember(state) {
                derivedStateOf { state.sugars.value }
            }
            TextField(
                value = TextFieldValue(sugars, TextRange(sugars.length)),
                onValueChange = { onSugarsChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp).focusRequester(sugarsRequester),
                label = { Text(stringResource(Res.string.nutriment_sugars)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.sugars.isValid,
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

            val saturatedFats by remember(state) {
                derivedStateOf { state.saturatedFats.value }
            }
            TextField(
                value = TextFieldValue(saturatedFats, TextRange(saturatedFats.length)),
                onValueChange = { onSaturatedFatsChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.saturatedFats.isValid,
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

            val salt by remember(state) {
                derivedStateOf { state.salt.value }
            }
            TextField(
                value = TextFieldValue(salt, TextRange(salt.length)),
                onValueChange = { onSaltChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_salt)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.salt.isValid,
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

            val sodium by remember(state) {
                derivedStateOf { state.sodium.value }
            }
            TextField(
                value = TextFieldValue(sodium, TextRange(sodium.length)),
                onValueChange = { onSodiumChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_sodium)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.sodium.isValid,
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

            val fiber by remember(state) {
                derivedStateOf { state.fiber.value }
            }
            TextField(
                value = TextFieldValue(fiber, TextRange(fiber.length)),
                onValueChange = { onFiberChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.nutriment_fiber)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.fiber.isValid,
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
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            val packageWeight by remember(state) {
                derivedStateOf { state.packageWeight.value }
            }
            TextField(
                value = TextFieldValue(packageWeight, TextRange(packageWeight.length)),
                onValueChange = { onPackageWeightChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_package_weight)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.packageWeight.isValid,
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

            val servingWeight by remember(state) {
                derivedStateOf { state.servingWeight.value }
            }
            TextField(
                value = TextFieldValue(servingWeight, TextRange(servingWeight.length)),
                onValueChange = { onServingWeightChange(it.text) },
                modifier = Modifier.widthIn(min = 300.dp),
                label = { Text(stringResource(Res.string.product_serving_weight)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                isError = !state.servingWeight.isValid,
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

            Spacer(Modifier.height(contentPadding.calculateBottomPadding()).fillMaxWidth())
            val height = LocalDensity.current.run { fabHeight.toDp() }
            Spacer(Modifier.height(height).fillMaxWidth())
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
