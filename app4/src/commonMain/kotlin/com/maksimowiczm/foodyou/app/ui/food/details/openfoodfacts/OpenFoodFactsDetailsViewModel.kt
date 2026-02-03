package com.maksimowiczm.foodyou.app.ui.food.details.openfoodfacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsUiState
import com.maksimowiczm.foodyou.app.ui.food.details.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.ui.food.details.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.common.domain.RemoteData
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class OpenFoodFactsDetailsViewModel(
    private val identity: OpenFoodFactsProductIdentity,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("OpenFoodFactsDetailsViewModel")

    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<FoodDetailsUiState<OpenFoodFactsProduct>> =
        combine(
                openFoodFactsRepository.observe(identity),
                observeIsFavoriteFoodUseCase.observe(identity),
                isRefreshing,
            ) { remoteData, isFavorite, isRefreshing ->
                when (remoteData) {
                    is RemoteData.Error<*> -> FoodDetailsUiState.Error(remoteData.error.message)
                    is RemoteData.Loading<OpenFoodFactsProduct> ->
                        FoodDetailsUiState.partial(remoteData.partialValue, isFavorite)

                    RemoteData.NotFound -> FoodDetailsUiState.NotFound
                    is RemoteData.Success<OpenFoodFactsProduct> ->
                        FoodDetailsUiState.Details(
                            food = remoteData.value,
                            isLoading = isRefreshing,
                            isFavorite = isFavorite,
                        )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(2_000),
                initialValue = FoodDetailsUiState.loading(),
            )

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                identity = FavoriteFoodIdentity.OpenFoodFacts(identity.barcode),
                isFavorite = isFavorite,
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(500)
            openFoodFactsRepository.refresh(identity).onError { error ->
                logger.e("Error refreshing OpenFoodFacts product: $identity", error)
            }
            isRefreshing.value = false
        }
    }
}
