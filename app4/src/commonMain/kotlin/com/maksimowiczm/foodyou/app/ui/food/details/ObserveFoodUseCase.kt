package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.app.ui.food.FoodIdentity
import com.maksimowiczm.foodyou.common.domain.RemoteData
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProduct
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class ObserveFoodUseCase(
    private val foodDataCentralRepository: FoodDataCentralRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val userFoodRepository: UserFoodRepository,
) {
    fun observe(identity: FoodIdentity): Flow<FoodDetailsUiState> =
        getRepository(identity).map { status -> status.toFoodDetailsUiState(identity) }

    private fun getRepository(identity: FoodIdentity): Flow<RemoteData<Any>> =
        when (identity) {
            is FoodIdentity.FoodDataCentral -> foodDataCentralRepository.observe(identity.identity)
            is FoodIdentity.OpenFoodFacts -> openFoodFactsRepository.observe(identity.identity)
            is FoodIdentity.UserFood ->
                userFoodRepository.observe(identity.identity).filterNotNull().map {
                    RemoteData.Success(it)
                }
        }

    private fun RemoteData<Any>.toFoodDetailsUiState(identity: FoodIdentity): FoodDetailsUiState =
        when (this) {
            is RemoteData.Success -> value.toUiState(isLoading = false)
            is RemoteData.Loading ->
                partialValue?.toUiState(isLoading = true)
                    ?: FoodDetailsUiState.WithData.loading(identity)
            is RemoteData.Error ->
                FoodDetailsUiState.Error(identity = identity, message = error.message)
            is RemoteData.NotFound -> FoodDetailsUiState.NotFound(identity = identity)
        }
}

private fun Any.toUiState(isLoading: Boolean): FoodDetailsUiState.WithData =
    when (this) {
        is FoodDataCentralProduct -> toUiState(isLoading)
        is OpenFoodFactsProduct -> toUiState(isLoading)
        is UserFoodProduct -> toUiState(isLoading)
        else -> error("Invalid product type: ${this::class.simpleName}")
    }

private fun FoodDataCentralProduct.toUiState(isLoading: Boolean) =
    FoodDetailsUiState.WithData(
        identity = identity,
        isLoading = isLoading,
        foodName = name,
        brand = brand,
        image = FoodImageUiState.NoImage,
        nutritionFacts = nutritionFacts,
        note = null,
        source = FoodSource.FoodDataCentral(source),
        isFavorite = false,
    )

private fun OpenFoodFactsProduct.toUiState(isLoading: Boolean) =
    FoodDetailsUiState.WithData(
        identity = identity,
        isLoading = isLoading,
        foodName = name,
        brand = brand,
        image = image?.let(FoodImageUiState::WithImage) ?: FoodImageUiState.NoImage,
        nutritionFacts = nutritionFacts,
        note = null,
        source = FoodSource.OpenFoodFacts(source),
        isFavorite = false,
    )

private fun UserFoodProduct.toUiState(isLoading: Boolean) =
    FoodDetailsUiState.WithData(
        identity = identity,
        isLoading = isLoading,
        foodName = name,
        brand = brand?.value,
        image = image?.let(FoodImageUiState::WithImage) ?: FoodImageUiState.NoImage,
        nutritionFacts = nutritionFacts,
        note = note,
        source = source?.value?.let(FoodSource::UserAdded),
        isFavorite = false,
    )
