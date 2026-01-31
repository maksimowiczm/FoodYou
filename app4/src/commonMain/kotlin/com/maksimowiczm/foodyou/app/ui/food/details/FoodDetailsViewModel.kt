package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.ui.food.FoodIdentity
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodDetailsViewModel(
    private val identity: FoodIdentity,
    observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
    private val foodDataCentralRepository: FoodDataCentralRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val userFoodRepository: UserFoodRepository,
    observeFoodUseCase: ObserveFoodUseCase,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("FoodDetailsViewModel")

    private val favoriteIdentity =
        when (identity) {
            is FoodIdentity.FoodDataCentral ->
                FavoriteFoodIdentity.FoodDataCentral(identity.identity.fdcId)

            is FoodIdentity.OpenFoodFacts ->
                FavoriteFoodIdentity.OpenFoodFacts(identity.identity.barcode)
            is FoodIdentity.UserFood -> FavoriteFoodIdentity.UserFoodProduct(identity.identity.id)
        }

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

    private val isRefreshing = MutableStateFlow(false)

    val uiState =
        observeFoodUseCase
            .observe(identity)
            .combine(isRefreshing) { uiState, refreshing ->
                if (refreshing && uiState is FoodDetailsUiState.WithData)
                    uiState.copy(isLoading = true)
                else uiState
            }
            .combine(profile.filterNotNull()) { uiState, profile ->
                if (uiState is FoodDetailsUiState.WithData)
                    uiState.copy(isFavorite = profile.isFavorite(favoriteIdentity))
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
                is FoodIdentity.FoodDataCentral -> {
                    isRefreshing.value = true
                    delay(500)
                    foodDataCentralRepository.refresh(identity.identity).onError {
                        // TODO
                        logger.e { "Error refreshing FoodDataCentral product: ${it.message}" }
                    }
                    isRefreshing.value = false
                }

                is FoodIdentity.OpenFoodFacts -> {
                    isRefreshing.value = true
                    delay(500)
                    openFoodFactsRepository.refresh(identity.identity).onError {
                        // TODO
                        logger.e { "Error refreshing OpenFoodFacts product: ${it.message}" }
                    }
                    isRefreshing.value = false
                }

                // Throw and hope user will report a bug
                is FoodIdentity.UserFood -> error("UserFood is not refreshable")
            }
        }
    }

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            val account = account()
            val profileId = accountManager.observePrimaryProfileId().first()

            account.updateProfile(profileId) {
                it.apply {
                    if (isFavorite) {
                        addFavoriteFood(favoriteIdentity)
                    } else {
                        removeFavoriteFood(favoriteIdentity)
                    }
                }
            }

            accountRepository.save(account)
        }
    }

    fun delete(userFoodProductIdentity: UserFoodProductIdentity) {
        viewModelScope.launch {
            userFoodRepository.delete(userFoodProductIdentity)
            eventChannel.send(FoodDetailsUiEvent.Deleted)
        }
    }
}
