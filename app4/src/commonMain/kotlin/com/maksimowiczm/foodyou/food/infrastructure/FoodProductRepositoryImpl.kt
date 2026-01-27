package com.maksimowiczm.foodyou.food.infrastructure

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.food.infrastructure.usda.FoodDataCentralRepository
import kotlinx.coroutines.flow.Flow

class FoodProductRepositoryImpl(
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val foodDataCentralRepository: FoodDataCentralRepository,
) : FoodProductRepository {
    override fun observe(identity: FoodProductIdentity): Flow<FoodProductRepository.FoodStatus> =
        when (identity) {
            is FoodProductIdentity.FoodDataCentral -> foodDataCentralRepository.observe(identity)
            is FoodProductIdentity.OpenFoodFacts -> openFoodFactsRepository.observe(identity)
        }

    override suspend fun refresh(
        identity: FoodProductIdentity.OpenFoodFacts
    ): Result<FoodProductDto, FoodDatabaseError> = openFoodFactsRepository.refresh(identity)

    override suspend fun refresh(
        identity: FoodProductIdentity.FoodDataCentral
    ): Result<FoodProductDto, FoodDatabaseError> = foodDataCentralRepository.refresh(identity)
}
