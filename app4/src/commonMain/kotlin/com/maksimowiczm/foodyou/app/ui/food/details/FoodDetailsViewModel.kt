package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.common.domain.RemoteData
import com.maksimowiczm.foodyou.common.onError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProduct
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodDetailsViewModel(
    private val identity: Any,
    observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
    private val foodDataCentralRepository: FoodDataCentralRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val userFoodRepository: UserFoodRepository,
    logger: Logger,
) : ViewModel() {
    private val logger = logger.withTag("FoodDetailsViewModel")

    private val favoriteIdentity =
        when (identity) {
            is FoodDataCentralProductIdentity ->
                FavoriteFoodIdentity.FoodDataCentral(identity.fdcId)
            is OpenFoodFactsProductIdentity -> FavoriteFoodIdentity.OpenFoodFacts(identity.barcode)
            is UserFoodProductIdentity -> FavoriteFoodIdentity.UserFoodProduct(identity.id)
            else -> error("Invalid identity")
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

    private val foodProduct: StateFlow<RemoteData<Any>> =
        when (identity) {
            is FoodDataCentralProductIdentity -> foodDataCentralRepository.observe(identity)
            is OpenFoodFactsProductIdentity -> openFoodFactsRepository.observe(identity)
            is UserFoodProductIdentity ->
                userFoodRepository.observe(identity).filterNotNull().map { RemoteData.Success(it) }

            else -> error("Invalid identity")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = RemoteData.Loading(null),
        )

    private val isRefreshing = MutableStateFlow(false)

    val uiState =
        foodProduct
            .map { status ->
                when (status) {
                    is RemoteData.Success -> status.value.toUiState(isLoading = false)

                    is RemoteData.Loading ->
                        status.partialValue?.toUiState(isLoading = true)
                            ?: FoodDetailsUiState.WithData.loading(identity)

                    is RemoteData.Error ->
                        FoodDetailsUiState.Error(
                            identity = identity,
                            message = status.error.message,
                        )

                    is RemoteData.NotFound -> FoodDetailsUiState.NotFound(identity = identity)
                }
            }
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
                is FoodDataCentralProductIdentity -> {
                    isRefreshing.value = true
                    delay(500)
                    foodDataCentralRepository.refresh(identity).onError {
                        // TODO
                        logger.e { "Error refreshing FoodDataCentral product: ${it.message}" }
                    }
                    isRefreshing.value = false
                }

                is OpenFoodFactsProductIdentity -> {
                    isRefreshing.value = true
                    delay(500)
                    openFoodFactsRepository.refresh(identity).onError {
                        // TODO
                        logger.e { "Error refreshing OpenFoodFacts product: ${it.message}" }
                    }
                    isRefreshing.value = false
                }
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

private fun Any.toUiState(isLoading: Boolean) =
    when (this) {
        is FoodDataCentralProduct -> this.toUiState(isLoading)
        is OpenFoodFactsProduct -> this.toUiState(isLoading)
        is UserFoodProduct -> this.toUiState(isLoading)
        else -> error("Invalid identity $this")
    }

private fun FoodDataCentralProduct.toUiState(isLoading: Boolean) =
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

private fun OpenFoodFactsProduct.toUiState(isLoading: Boolean) =
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

private fun UserFoodProduct.toUiState(isLoading: Boolean) =
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
