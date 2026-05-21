package com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsUiState
import com.maksimowiczm.foodyou.app.ui.food.details.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.ui.food.details.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FoodDataCentralDetailsViewModel(
    private val identity: FoodDataCentralProductIdentity,
    private val service: FoodDataCentralService,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("FoodDataCentralDetailsViewModel")
    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<FoodDetailsUiState<FoodDataCentralProduct>> =
        combine(
                service.observe(identity),
                observeIsFavoriteFoodUseCase.observe(identity),
                isRefreshing,
            ) { remoteData, isFavorite, isRefreshing ->
                when (remoteData) {
                    is RemoteData.Error<*> -> FoodDetailsUiState.Error(remoteData.error.message)
                    is RemoteData.Loading<FoodDataCentralProduct> ->
                        FoodDetailsUiState.partial(remoteData.partialValue, isFavorite)

                    RemoteData.NotFound -> FoodDetailsUiState.NotFound
                    is RemoteData.Success<FoodDataCentralProduct> ->
                        FoodDetailsUiState.Details(
                            food = remoteData.value,
                            isLoading = isRefreshing,
                            isFavorite = isFavorite,
                        )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = FoodDetailsUiState.loading(),
            )

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                identity = FavoriteFoodIdentity.FoodDataCentral(identity.fdcId),
                isFavorite = isFavorite,
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(500)
            service.refresh(identity).onError { error ->
                logger.e("Error refreshing FoodDataCentral product: $identity", error)
            }
            isRefreshing.value = false
        }
    }
}
