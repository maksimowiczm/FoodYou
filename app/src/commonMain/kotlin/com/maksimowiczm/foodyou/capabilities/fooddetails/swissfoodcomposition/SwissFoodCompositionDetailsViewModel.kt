package com.maksimowiczm.foodyou.capabilities.fooddetails.swissfoodcomposition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodId
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsQuantityDelegate
import com.maksimowiczm.foodyou.capabilities.fooddetails.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.capabilities.fooddetails.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProductId
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionRepository
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.nutritionFacts
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SwissFoodCompositionDetailsViewModel(
    private val id: SwissFoodCompositionProductId,
    initialQuantity: Quantity?,
    repository: SwissFoodCompositionRepository,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val quantityDelegate = FoodDetailsQuantityDelegate(savedStateHandle, initialQuantity)

    val uiState: StateFlow<SwissFoodCompositionDetailsUiState> =
        combine(
                repository.observe(id),
                observeIsFavoriteFoodUseCase.observe(id),
                quantityDelegate.selectedQuantityType,
                quantityDelegate.selectedQuantity,
            ) { product, isFavorite, selectedQuantityType, selectedQuantity ->
                if (product == null) {
                    return@combine SwissFoodCompositionDetailsUiState.NotFound
                }

                val result =
                    quantityDelegate.calculate(
                        product =
                            FoodDetailsQuantityDelegate.QuantityProvider(
                                servingQuantity = null,
                                packageQuantity = null,
                                nutritionFacts = product.nutritionFacts,
                                isLiquid = false,
                            ),
                        currentSelectedQuantity = selectedQuantity,
                        currentSelectedQuantityType = selectedQuantityType,
                    )

                SwissFoodCompositionDetailsUiState.Details(
                    food = product,
                    isFavorite = isFavorite,
                    suggestions = result.suggestions,
                    selectedQuantity = result.selectedQuantity,
                    scaledNutritionFacts = result.scaledNutritionFacts,
                    quantityTypes = result.quantityTypes,
                    selectedQuantityType = result.selectedQuantityType,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2.seconds),
                initialValue = SwissFoodCompositionDetailsUiState.Loading,
            )

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                id = FavoriteFoodId.SwissFoodComposition(id.value),
                isFavorite = isFavorite,
            )
        }
    }

    fun selectQuantity(quantity: Quantity) {
        quantityDelegate.selectQuantity(quantity)
    }

    fun selectQuantity(amount: Double?, type: QuantityType?) {
        if (amount != null && amount > 0.0 && type != null) {
            selectQuantity(type.toQuantity(amount))
        }
    }

    fun selectQuantityType(type: QuantityType) {
        quantityDelegate.selectQuantityType(type)
    }
}
