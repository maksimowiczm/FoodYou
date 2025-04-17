package com.maksimowiczm.foodyou.feature.productredesign

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.input.Input
import com.maksimowiczm.foodyou.core.ui.ext.toDp
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

// TODO
//  When downloading a product set selection to the end of the text field
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ProductForm(
    state: ProductFormState,
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
    onUseOpenFoodFactsProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current

    var fabHeight by remember { mutableIntStateOf(0) }
    val sugarsRequester = remember { FocusRequester() }

    val insets = remember(contentPadding) {
        WindowInsets(
            left = contentPadding.calculateLeftPadding(layoutDirection),
            right = contentPadding.calculateRightPadding(layoutDirection)
        )
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = insets
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(contentPadding.calculateTopPadding()).fillMaxWidth())

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FlowRow {
                    AssistChip(
                        onClick = onUseOpenFoodFactsProduct,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                        label = {
                            Text(stringResource(Res.string.action_use_open_food_facts_product))
                        }
                    )
                }

                Text(
                    text = stringResource(Res.string.headline_general),
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

                TextField(
                    value = state.name.value,
                    onValueChange = onNameChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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

                TextField(
                    value = state.brand.value,
                    onValueChange = onBrandChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
                    label = { Text(stringResource(Res.string.product_brand)) },
                    supportingText = { Spacer(Modifier.height(LocalTextStyle.current.toDp())) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )

                TextField(
                    value = state.barcode.value,
                    onValueChange = onBarcodeChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

                TextField(
                    value = state.proteins.value,
                    onValueChange = onProteinsChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
                    label = { Text(stringResource(Res.string.nutriment_proteins)) },
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    isError = state.proteins.isInvalid,
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

                TextField(
                    value = state.carbohydrates.value,
                    onValueChange = onCarbohydratesChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
                    label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    isError = state.carbohydrates.isInvalid,
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

                TextField(
                    value = state.fats.value,
                    onValueChange = onFatsChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
                    label = { Text(stringResource(Res.string.nutriment_fats)) },
                    suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                    isError = state.fats.isInvalid,
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

                TextField(
                    value = state.calories?.formatClipZeros() ?: "",
                    onValueChange = {},
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )

                TextField(
                    value = state.sugars.value,
                    onValueChange = onSugarsChange,
                    modifier = Modifier
                        .widthIn(min = 300.dp)
                        .fillMaxWidth()
                        .focusRequester(sugarsRequester),
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

                TextField(
                    value = state.saturatedFats.value,
                    onValueChange = onSaturatedFatsChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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

                TextField(
                    value = state.salt.value,
                    onValueChange = onSaltChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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

                TextField(
                    value = state.sodium.value,
                    onValueChange = onSodiumChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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

                TextField(
                    value = state.fiber.value,
                    onValueChange = onFiberChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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

                TextField(
                    value = state.packageWeight.value,
                    onValueChange = onPackageWeightChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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

                TextField(
                    value = state.servingWeight.value,
                    onValueChange = onServingWeightChange,
                    modifier = Modifier.widthIn(min = 300.dp).fillMaxWidth(),
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
