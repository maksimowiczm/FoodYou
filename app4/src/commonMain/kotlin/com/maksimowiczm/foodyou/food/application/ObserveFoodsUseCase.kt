package com.maksimowiczm.foodyou.food.application

import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.domain.FoodStatus
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ObserveFoodsUseCase(private val productRepository: FoodProductRepository) {
    fun observe(vararg identity: FoodProductIdentity): Flow<List<FoodStatus<FoodProductDto>>> {
        if (identity.isEmpty()) {
            return flowOf(emptyList())
        }

        val identities = identity.toSet()

        val flows =
            identities.map { identity ->
                val params =
                    when (identity) {
                        is FoodProductIdentity.FoodDataCentral ->
                            QueryParameters.FoodDataCentral(identity)

                        is FoodProductIdentity.Local -> QueryParameters.Local(identity)
                        is FoodProductIdentity.OpenFoodFacts ->
                            QueryParameters.OpenFoodFacts(identity)
                    }

                productRepository.observe(params)
            }

        return flows.combine()
    }
}
