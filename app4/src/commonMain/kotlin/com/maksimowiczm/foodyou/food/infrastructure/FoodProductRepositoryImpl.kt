package com.maksimowiczm.foodyou.food.infrastructure

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.domain.LocalFoodDeletedEvent
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.food.infrastructure.usda.FoodDataCentralRepository
import com.maksimowiczm.foodyou.food.infrastructure.user.UserFoodRepositoryImpl
import kotlinx.coroutines.flow.Flow

class FoodProductRepositoryImpl(
    private val userFoodRepository: UserFoodRepositoryImpl,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val foodDataCentralRepository: FoodDataCentralRepository,
    private val integrationEventBus: EventBus<IntegrationEvent>,
) : FoodProductRepository {
    override fun observe(identity: FoodProductIdentity): Flow<FoodProductRepository.FoodStatus> =
        when (identity) {
            is FoodProductIdentity.FoodDataCentral -> foodDataCentralRepository.observe(identity)
            is FoodProductIdentity.Local -> userFoodRepository.observeFoodStatus(identity)
            is FoodProductIdentity.OpenFoodFacts -> openFoodFactsRepository.observe(identity)
        }

    override suspend fun refresh(
        identity: FoodProductIdentity.OpenFoodFacts
    ): Result<FoodProductDto, FoodDatabaseError> = openFoodFactsRepository.refresh(identity)

    override suspend fun refresh(
        identity: FoodProductIdentity.FoodDataCentral
    ): Result<FoodProductDto, FoodDatabaseError> = foodDataCentralRepository.refresh(identity)

    override suspend fun delete(identity: FoodProductIdentity.Local) {
        userFoodRepository.delete(identity)
        integrationEventBus.publish(LocalFoodDeletedEvent(identity))
    }
}
