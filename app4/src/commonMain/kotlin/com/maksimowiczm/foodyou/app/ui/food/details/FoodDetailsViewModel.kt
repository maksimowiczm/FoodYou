package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.domain.FoodStatus
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodDetailsViewModel(
    private val identity: FoodProductIdentity,
    private val foodProductRepository: FoodProductRepository,
    observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("FoodDetailsViewModel")

    private val account =
        observePrimaryAccountUseCase
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    private suspend fun account() = account.filterNotNull().first()

    private val profile =
        combine(account, accountManager.observePrimaryProfileId()) { account, profileId ->
                account?.profiles?.find { it.id == profileId }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    private val eventChannel = Channel<FoodDetailsUiEvent>()
    val uiEvents = eventChannel.receiveAsFlow()

    private val queryParameters: QueryParameters =
        when (identity) {
            is FoodProductIdentity.FoodDataCentral -> QueryParameters.FoodDataCentral(identity)

            is FoodProductIdentity.Local -> QueryParameters.Local(identity)

            is FoodProductIdentity.OpenFoodFacts -> QueryParameters.OpenFoodFacts(identity)
        }

    private val foodProduct =
        foodProductRepository
            .observe(queryParameters)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = FoodStatus.Loading(identity, null),
            )

    private val isRefreshing = MutableStateFlow(false)

    val uiState =
        foodProduct
            .map { status ->
                when (status) {
                    is FoodStatus.Available ->
                        status.food.toUiState(identity = identity, isLoading = false)

                    is FoodStatus.Loading ->
                        status.food?.toUiState(identity = identity, isLoading = true)
                            ?: FoodDetailsUiState.WithData.loading(identity)

                    is FoodStatus.Error ->
                        FoodDetailsUiState.Error(
                            identity = identity,
                            message = status.error.message,
                        )

                    is FoodStatus.NotFound -> FoodDetailsUiState.NotFound(identity = identity)
                }
            }
            .combine(isRefreshing) { uiState, refreshing ->
                if (refreshing && uiState is FoodDetailsUiState.WithData)
                    uiState.copy(isLoading = true)
                else uiState
            }
            .combine(profile.filterNotNull()) { uiState, profile ->
                if (uiState is FoodDetailsUiState.WithData)
                    uiState.copy(isFavorite = profile.isFavorite(identity))
                else uiState
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
                        isFavorite = false,
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

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            val account = account()
            val profileId = accountManager.observePrimaryProfileId().first()

            account.updateProfile(profileId) {
                it.apply {
                    if (isFavorite) {
                        addFavoriteFood(identity)
                    } else {
                        removeFavoriteFood(identity)
                    }
                }
            }

            accountRepository.save(account)
        }
    }
}

private fun FoodProductDto.toUiState(identity: FoodProductIdentity, isLoading: Boolean) =
    FoodDetailsUiState.WithData(
        identity = identity,
        isLoading = isLoading,
        foodName = name,
        brand = brand?.value,
        image = image?.let { FoodImageUiState.WithImage(it) } ?: FoodImageUiState.NoImage,
        nutritionFacts = nutritionFacts,
        note = note,
        source = source,
        isFavorite = false,
    )
