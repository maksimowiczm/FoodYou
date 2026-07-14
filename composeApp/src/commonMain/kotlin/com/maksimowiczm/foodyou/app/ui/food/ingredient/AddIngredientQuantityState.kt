package com.maksimowiczm.foodyou.app.ui.food.ingredient

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.common.component.QuantityType
import com.maksimowiczm.foodyou.app.ui.common.component.rememberQuantityFormField
import com.maksimowiczm.foodyou.app.ui.common.form.FormField
import com.maksimowiczm.foodyou.app.ui.common.utility.formatCompact
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces

@Stable
class AddIngredientQuantityState(
    initialQuantityType: QuantityType,
    initialQuantity: Quantity,
    val formField: FormField,
    val quantitySuggestions: List<Quantity>,
    val quantityTypes: List<QuantityType>,
) {
    var selectedQuantityType: QuantityType by mutableStateOf(initialQuantityType)
        private set

    var quantity: Quantity by mutableStateOf(initialQuantity)
        private set

    fun onSelectedQuantityTypeChange(type: QuantityType) {
        selectedQuantityType = type
    }

    fun onSelectQuantity(quantity: Quantity) {
        when (quantity) {
            is AbsoluteQuantity.Weight -> {
                selectedQuantityType =
                    if (quantity.weight.unit == WeightUnit.Ounces) QuantityType.Ounce
                    else QuantityType.Gram
                formField.textFieldState.setTextAndPlaceCursorAtEnd(
                    quantity.weight.toDouble(quantity.weight.unit).formatCompact()
                )
            }
            is AbsoluteQuantity.Volume -> {
                selectedQuantityType =
                    if (quantity.volume.unit == VolumeUnit.FluidOunces) QuantityType.FluidOunce
                    else QuantityType.Milliliter
                formField.textFieldState.setTextAndPlaceCursorAtEnd(
                    quantity.volume.toDouble(quantity.volume.unit).formatCompact()
                )
            }
            is ServingQuantity -> {
                selectedQuantityType = QuantityType.Serving
                formField.textFieldState.setTextAndPlaceCursorAtEnd(
                    quantity.servings.formatCompact()
                )
            }
            is PackageQuantity -> {
                selectedQuantityType = QuantityType.Package
                formField.textFieldState.setTextAndPlaceCursorAtEnd(
                    quantity.packages.formatCompact()
                )
            }
        }
    }

    internal fun updateQuantity(quantity: Quantity) {
        this.quantity = quantity
    }
}

@Composable
fun rememberAddIngredientQuantityState(
    initialQuantity: Quantity?,
    servingQuantity: AbsoluteQuantity?,
    packageQuantity: AbsoluteQuantity?,
    isLiquid: Boolean,
): AddIngredientQuantityState {
    val initialQuantityType =
        remember(initialQuantity, servingQuantity, packageQuantity, isLiquid) {
            when (initialQuantity) {
                is AbsoluteQuantity.Weight ->
                    if (initialQuantity.weight.unit == WeightUnit.Ounces) QuantityType.Ounce
                    else QuantityType.Gram
                is AbsoluteQuantity.Volume ->
                    if (initialQuantity.volume.unit == VolumeUnit.FluidOunces)
                        QuantityType.FluidOunce
                    else QuantityType.Milliliter
                is ServingQuantity -> QuantityType.Serving
                is PackageQuantity -> QuantityType.Package
                null ->
                    when {
                        servingQuantity != null -> QuantityType.Serving
                        packageQuantity != null -> QuantityType.Package
                        isLiquid -> QuantityType.Milliliter
                        else -> QuantityType.Gram
                    }
            }
        }

    val resolvedInitialQuantity =
        remember(initialQuantity, servingQuantity, packageQuantity, isLiquid) {
            initialQuantity
                ?: when {
                    servingQuantity != null -> ServingQuantity(1.0)
                    packageQuantity != null -> PackageQuantity(1.0)
                    isLiquid -> AbsoluteQuantity.Volume(100.milliliters)
                    else -> AbsoluteQuantity.Weight(100.grams)
                }
        }

    val defaultValue =
        remember(initialQuantity, servingQuantity, packageQuantity) {
            when (initialQuantity) {
                is AbsoluteQuantity.Weight ->
                    initialQuantity.weight.toDouble(initialQuantity.weight.unit)
                is AbsoluteQuantity.Volume ->
                    initialQuantity.volume.toDouble(initialQuantity.volume.unit)
                is ServingQuantity -> initialQuantity.servings
                is PackageQuantity -> initialQuantity.packages
                null -> if (servingQuantity != null || packageQuantity != null) 1.0 else 100.0
            }.formatCompact()
        }

    val formField =
        rememberQuantityFormField(
            servingQuantity,
            packageQuantity,
            isLiquid,
            initialQuantity,
            defaultValue = defaultValue,
        )

    val quantitySuggestions =
        remember(servingQuantity, packageQuantity, isLiquid) {
            buildList {
                if (isLiquid) add(AbsoluteQuantity.Volume(100.milliliters))
                else add(AbsoluteQuantity.Weight(100.grams))
                if (servingQuantity != null) add(ServingQuantity(1.0))
                if (packageQuantity != null) add(PackageQuantity(1.0))
            }
        }

    val quantityTypes =
        remember(servingQuantity, packageQuantity, isLiquid) {
            buildList {
                if (isLiquid) {
                    add(QuantityType.Milliliter)
                    add(QuantityType.FluidOunce)
                } else {
                    add(QuantityType.Gram)
                    add(QuantityType.Ounce)
                }
                if (servingQuantity != null) add(QuantityType.Serving)
                if (packageQuantity != null) add(QuantityType.Package)
            }
        }

    val state =
        rememberSaveable(
            formField,
            quantitySuggestions,
            quantityTypes,
            resolvedInitialQuantity,
            saver =
                Saver(
                    save = { state ->
                        listOf(
                            state.selectedQuantityType.ordinal,
                            state.formField.textFieldState.text.toString(),
                        )
                    },
                    restore = { saved ->
                        val restoredType = QuantityType.entries[saved[0] as Int]
                        val restoredText = saved[1] as String
                        formField.textFieldState.setTextAndPlaceCursorAtEnd(restoredText)
                        AddIngredientQuantityState(
                            initialQuantityType = restoredType,
                            initialQuantity = resolvedInitialQuantity,
                            formField = formField,
                            quantitySuggestions = quantitySuggestions,
                            quantityTypes = quantityTypes,
                        )
                    },
                ),
        ) {
            AddIngredientQuantityState(
                initialQuantityType = initialQuantityType,
                initialQuantity = resolvedInitialQuantity,
                formField = formField,
                quantitySuggestions = quantitySuggestions,
                quantityTypes = quantityTypes,
            )
        }

    LaunchedEffect(formField.textFieldState.text, state.selectedQuantityType) {
        val amount = formField.textFieldState.text.toString().toDoubleOrNull()
        if (amount != null && amount > 0.0) {
            state.updateQuantity(
                when (state.selectedQuantityType) {
                    QuantityType.Gram -> AbsoluteQuantity.Weight(amount.grams)
                    QuantityType.Ounce -> AbsoluteQuantity.Weight(amount.ounces)
                    QuantityType.Milliliter -> AbsoluteQuantity.Volume(amount.milliliters)
                    QuantityType.FluidOunce -> AbsoluteQuantity.Volume(amount.fluidOunces)
                    QuantityType.Serving -> ServingQuantity(amount)
                    QuantityType.Package -> PackageQuantity(amount)
                }
            )
        }
    }

    return state
}
