package com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodId
import com.maksimowiczm.foodyou.app.application.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.application.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsQuantityDelegate
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCommand
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import kotlin.time.Clock
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserProductDetailsViewModel(
    private val id: UserProductId,
    initialQuantity: Quantity?,
    private val userProductService: UserProductService,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventChannel = Channel<UserProductDetailsUiEvent>()
    val uiEvents = eventChannel.receiveAsFlow()

    private val quantityDelegate = FoodDetailsQuantityDelegate(savedStateHandle, initialQuantity)

    val uiState =
        combine(
                userProductService.observe(id),
                observeIsFavoriteFoodUseCase.observe(id),
                quantityDelegate.selectedQuantityType,
                quantityDelegate.selectedQuantity,
            ) { product, isFavorite, selectedQuantityType, selectedQuantity ->
                if (product == null) {
                    return@combine null
                }

                val result =
                    quantityDelegate.calculate(
                        product =
                            FoodDetailsQuantityDelegate.QuantityProvider(
                                product.servingQuantity,
                                product.packageQuantity,
                                product.nutritionFacts,
                                product.isLiquid,
                            ),
                        currentSelectedQuantity = selectedQuantity,
                        currentSelectedQuantityType = selectedQuantityType,
                    )

                UserProductDetailsUiState(
                    product = product,
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
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

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

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                id = FavoriteFoodId.UserProduct(id.value),
                isFavorite = isFavorite,
            )
        }
    }

    fun delete() {
        viewModelScope.launch {
            userProductService.handle(
                id = id,
                command =
                    UserProductCommand.Remove(
                        strategy = DeleteStrategy.Delete,
                        timestamp = Clock.System.now(),
                    ),
            )
            eventChannel.send(UserProductDetailsUiEvent.Deleted)
        }
    }
}
