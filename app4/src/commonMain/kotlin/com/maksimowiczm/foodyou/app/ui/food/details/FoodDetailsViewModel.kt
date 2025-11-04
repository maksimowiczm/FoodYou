package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository.FoodStatus
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodDetailsViewModel(
    private val identity: FoodProductIdentity,
    private val foodProductRepository: FoodProductRepository,
    accountManager: AccountManager,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("FoodDetailsViewModel")

    private val eventChannel = Channel<FoodDetailsUiEvent>()
    val uiEvents = eventChannel.receiveAsFlow()

    private val queryParameters: StateFlow<QueryParameters?> =
        when (identity) {
            is FoodProductIdentity.FoodDataCentral ->
                flowOf(QueryParameters.FoodDataCentral(identity))

            is FoodProductIdentity.Local -> {
                combine(
                    accountManager.observePrimaryAccountId().filterNotNull(),
                    accountManager.observePrimaryProfileId(),
                ) { accountId, profileId ->
                    QueryParameters.Local(
                        identity = identity,
                        accountId = accountId,
                        profileId = profileId,
                    )
                }
            }

            is FoodProductIdentity.OpenFoodFacts -> flowOf(QueryParameters.OpenFoodFacts(identity))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null,
        )

    private val foodProduct =
        queryParameters
            .filterNotNull()
            .flatMapLatest { params -> foodProductRepository.observe(params) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = FoodStatus.Loading(identity, null),
            )

    private val isRefreshing = MutableStateFlow(false)

    val uiState =
        foodProduct
            .map {
                when (it) {
                    is FoodStatus.Available -> {
                        val image =
                            when {
                                it.food.image != null -> FoodImageUiState.WithImage(it.food.image)
                                else -> FoodImageUiState.NoImage
                            }

                        FoodDetailsUiState.WithData(
                            identity = identity,
                            isLoading = false,
                            foodName = it.food.name,
                            brand = it.food.brand?.value,
                            image = image,
                            nutritionFacts = it.food.nutritionFacts,
                            note = it.food.note,
                            source = it.food.source,
                        )
                    }

                    is FoodStatus.Error ->
                        FoodDetailsUiState.Error(identity = identity, message = it.error.message)

                    is FoodStatus.Loading -> {
                        val image =
                            when {
                                it.food?.image != null -> FoodImageUiState.WithImage(it.food.image)
                                else -> FoodImageUiState.Loading
                            }

                        FoodDetailsUiState.WithData(
                            identity = identity,
                            isLoading = true,
                            foodName = it.food?.name,
                            brand = it.food?.brand?.value,
                            image = image,
                            nutritionFacts = it.food?.nutritionFacts,
                            note = it.food?.note,
                            source = it.food?.source,
                        )
                    }

                    FoodStatus.NotFound -> FoodDetailsUiState.NotFound(identity = identity)
                }
            }
            .combine(isRefreshing) { uiState, refreshing ->
                if (refreshing && uiState is FoodDetailsUiState.WithData) {
                    FoodDetailsUiState.WithData(
                        isLoading = true,
                        identity = uiState.identity,
                        foodName = uiState.foodName,
                        brand = uiState.brand,
                        image = uiState.image,
                        nutritionFacts = uiState.nutritionFacts,
                        note = uiState.note,
                        source = uiState.source,
                    )
                } else {
                    uiState
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue =
                    FoodDetailsUiState.WithData(
                        isLoading = true,
                        identity = identity,
                        foodName = null,
                        brand = null,
                        image = FoodImageUiState.Loading,
                        nutritionFacts = null,
                        note = null,
                        source = null,
                    ),
            )

    fun refresh() {
        viewModelScope.launch {
            when (identity) {
                is FoodProductIdentity.FoodDataCentral -> {
                    isRefreshing.value = true
                    delay(500)
                    foodProductRepository.refresh(identity).onError {
                        // TODO
                        logger.e { "Error refreshing FoodDataCentral product: ${it.message}" }
                    }
                    isRefreshing.value = false
                }

                is FoodProductIdentity.Local ->
                    logger.w { "Refresh is not supported for local food products" }

                is FoodProductIdentity.OpenFoodFacts -> {
                    isRefreshing.value = true
                    delay(500)
                    foodProductRepository.refresh(identity).onError {
                        // TODO
                        logger.e { "Error refreshing OpenFoodFacts product: ${it.message}" }
                    }
                    isRefreshing.value = false
                }
            }
        }
    }

    fun delete() {
        require(identity is FoodProductIdentity.Local) {
            "Delete is only supported for local food products"
        }

        viewModelScope.launch {
            foodProductRepository.delete(identity)
            eventChannel.send(FoodDetailsUiEvent.Deleted)
        }
    }
}
