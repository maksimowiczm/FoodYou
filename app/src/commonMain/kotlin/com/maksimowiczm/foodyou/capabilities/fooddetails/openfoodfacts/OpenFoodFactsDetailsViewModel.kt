package com.maksimowiczm.foodyou.capabilities.fooddetails.openfoodfacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.application.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.application.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsQuantityDelegate
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OpenFoodFactsDetailsViewModel(
    private val identity: OpenFoodFactsProductIdentity,
    initialQuantity: Quantity?,
    private val openFoodFactsService: OpenFoodFactsService,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
    savedStateHandle: SavedStateHandle,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("OpenFoodFactsDetailsViewModel")

    private val isRefreshing = MutableStateFlow(false)

    private val quantityDelegate = FoodDetailsQuantityDelegate(savedStateHandle, initialQuantity)

    val uiState: StateFlow<OpenFoodFactsDetailsUiState> =
        combine(
                openFoodFactsService.observe(identity),
                observeIsFavoriteFoodUseCase.observe(identity),
                isRefreshing,
                quantityDelegate.selectedQuantityType,
                quantityDelegate.selectedQuantity,
            ) { remoteData, isFavorite, isRefreshing, selectedQuantityType, selectedQuantity ->
                when (remoteData) {
                    is RemoteData.Error<*> ->
                        OpenFoodFactsDetailsUiState.Error(remoteData.error.message)

                    RemoteData.NotFound -> OpenFoodFactsDetailsUiState.NotFound
                    is RemoteData.Loading<OpenFoodFactsProduct> ->
                        OpenFoodFactsDetailsUiState.Loading

                    is RemoteData.Success<OpenFoodFactsProduct> -> {
                        val food = remoteData.value

                        val result =
                            quantityDelegate.calculate(
                                product =
                                    FoodDetailsQuantityDelegate.QuantityProvider(
                                        food.servingQuantity,
                                        food.packageQuantity,
                                        food.nutritionFacts,
                                        false,
                                    ),
                                currentSelectedQuantity = selectedQuantity,
                                currentSelectedQuantityType = selectedQuantityType,
                            )

                        OpenFoodFactsDetailsUiState.Details(
                            food = food,
                            isFavorite = isFavorite,
                            isLoading = isRefreshing,
                            suggestions = result.suggestions,
                            selectedQuantity = result.selectedQuantity,
                            scaledNutritionFacts = result.scaledNutritionFacts,
                            quantityTypes = result.quantityTypes,
                            selectedQuantityType = result.selectedQuantityType,
                        )
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = OpenFoodFactsDetailsUiState.Loading,
            )

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                identity = FavoriteFoodIdentity.OpenFoodFacts(identity.barcode),
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

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(500.milliseconds)
            openFoodFactsService.refresh(identity).onError { error ->
                logger.e("Error refreshing OpenFoodFacts product: $identity", error)
            }
            isRefreshing.value = false
        }
    }
}
