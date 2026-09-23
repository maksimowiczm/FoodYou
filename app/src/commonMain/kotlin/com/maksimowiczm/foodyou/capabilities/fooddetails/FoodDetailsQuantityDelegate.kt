package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.lifecycle.SavedStateHandle
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.AvailableQuantityTypesService
import com.maksimowiczm.foodyou.common.domain.food.InitialQuantityCalculator
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantitySuggestionsCalculator
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.domain.food.type
import com.maksimowiczm.foodyou.common.getOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class FoodDetailsQuantityDelegate(
    savedStateHandle: SavedStateHandle,
    private val initialQuantity: Quantity?,
) {
    val selectedQuantityType =
        savedStateHandle.getMutableStateFlow<QuantityType?>(SELECTED_QUANTITY_TYPE_KEY, null)

    private val selectedQuantityJson =
        savedStateHandle.getMutableStateFlow<String?>(SELECTED_QUANTITY_KEY, null)
    val selectedQuantity = selectedQuantityJson.map {
        it?.let { Json.decodeFromString<Quantity>(it) }
    }

    fun calculate(
        product: QuantityProvider,
        currentSelectedQuantity: Quantity?,
        currentSelectedQuantityType: QuantityType?,
    ): QuantityCalculationResult {
        val selectedQuantity =
            currentSelectedQuantity
                ?: initialQuantity
                ?: InitialQuantityCalculator.calculate(
                    servingQuantity = product.servingQuantity,
                    packageQuantity = product.packageQuantity,
                    isLiquid = product.isLiquid,
                )

        val suggestions =
            (QuantitySuggestionsCalculator.getSuggestions(
                    isLiquid = product.isLiquid,
                    servingQuantity = product.servingQuantity,
                    packageQuantity = product.packageQuantity,
                ) + initialQuantity)
                .filterNotNull()
                .distinct()

        val quantityTypes =
            AvailableQuantityTypesService.getAvailableTypes(
                isLiquid = product.isLiquid,
                servingQuantity = product.servingQuantity,
                packageQuantity = product.packageQuantity,
            )

        val scaledNutritionFacts =
            product.nutritionFacts
                .scale(
                    packageQuantity = product.packageQuantity,
                    servingQuantity = product.servingQuantity,
                    quantity = selectedQuantity,
                )
                .getOrNull()

        return QuantityCalculationResult(
            suggestions = suggestions,
            selectedQuantity = selectedQuantity,
            scaledNutritionFacts = scaledNutritionFacts,
            quantityTypes = quantityTypes,
            selectedQuantityType = currentSelectedQuantityType ?: selectedQuantity.type,
        )
    }

    fun selectQuantity(quantity: Quantity) {
        selectedQuantityJson.value = Json.encodeToString(quantity)
        selectedQuantityType.value = quantity.type
    }

    fun selectQuantityType(type: QuantityType) {
        selectedQuantityType.value = type
    }

    companion object {
        private const val SELECTED_QUANTITY_KEY = "selected_quantity"
        private const val SELECTED_QUANTITY_TYPE_KEY = "selected_quantity_type"
    }

    data class QuantityProvider(
        val servingQuantity: AbsoluteQuantity?,
        val packageQuantity: AbsoluteQuantity?,
        val nutritionFacts: NutritionFacts,
        val isLiquid: Boolean,
    )

    data class QuantityCalculationResult(
        val suggestions: List<Quantity>,
        val selectedQuantity: Quantity,
        val scaledNutritionFacts: NutritionFacts?,
        val quantityTypes: List<QuantityType>,
        val selectedQuantityType: QuantityType,
    )
}
