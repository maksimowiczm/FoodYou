package com.maksimowiczm.foodyou.food.infrastructure

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.food.infrastructure.usda.FoodDataCentralRepository
import com.maksimowiczm.foodyou.food.infrastructure.user.UserFoodRepository
import kotlinx.coroutines.flow.Flow

class FoodProductRepositoryImpl(
    private val userFoodRepository: UserFoodRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val foodDataCentralRepository: FoodDataCentralRepository,
) : FoodProductRepository {
    override suspend fun observe(
        queryParameters: QueryParameters
    ): Flow<FoodProductRepository.FoodStatus> =
        when (queryParameters) {
            is QueryParameters.FoodDataCentral -> foodDataCentralRepository.observe(queryParameters)
            is QueryParameters.Local -> userFoodRepository.observe(queryParameters)
            is QueryParameters.OpenFoodFacts -> openFoodFactsRepository.observe(queryParameters)
        }

    override suspend fun refresh(
        identity: FoodProductIdentity.OpenFoodFacts
    ): Result<FoodProductDto, FoodDatabaseError> = openFoodFactsRepository.refresh(identity)

    override suspend fun refresh(
        identity: FoodProductIdentity.FoodDataCentral
    ): Result<FoodProductDto, FoodDatabaseError> = foodDataCentralRepository.refresh(identity)
}
