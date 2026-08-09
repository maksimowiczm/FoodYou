package com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral

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
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodDataCentralDetailsViewModel(
    private val identity: FoodDataCentralProductIdentity,
    initialQuantity: Quantity?,
    private val service: FoodDataCentralService,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
    savedStateHandle: SavedStateHandle,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("FoodDataCentralDetailsViewModel")
    private val isRefreshing = MutableStateFlow(false)
    private val quantityDelegate = FoodDetailsQuantityDelegate(savedStateHandle, initialQuantity)

    val uiState: StateFlow<FoodDataCentralDetailsUiState> =
        combine(
                service.observe(identity),
                observeIsFavoriteFoodUseCase.observe(identity),
                isRefreshing,
                quantityDelegate.selectedQuantityType,
                quantityDelegate.selectedQuantity,
            ) { remoteData, isFavorite, isRefreshing, selectedQuantityType, selectedQuantity ->
                when (remoteData) {
                    is RemoteData.Error<*> ->
                        FoodDataCentralDetailsUiState.Error(remoteData.error.message)

                    RemoteData.NotFound -> FoodDataCentralDetailsUiState.NotFound
                    is RemoteData.Loading<FoodDataCentralProduct> ->
                        FoodDataCentralDetailsUiState.Loading

                    is RemoteData.Success<FoodDataCentralProduct> -> {
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

                        FoodDataCentralDetailsUiState.Details(
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
                initialValue = FoodDataCentralDetailsUiState.Loading,
            )

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                identity = FavoriteFoodIdentity.FoodDataCentral(identity.fdcId),
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
            service.refresh(identity).onError { error ->
                logger.e("Error refreshing FoodDataCentral product: $identity", error)
            }
            isRefreshing.value = false
        }
    }
}
