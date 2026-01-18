package com.maksimowiczm.foodyou.food.application

import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ObserveFoodsUseCase(private val productRepository: FoodProductRepository) {
    fun observe(
        vararg identity: FoodProductIdentity
    ): Flow<List<FoodProductRepository.FoodStatus>> {
        if (identity.isEmpty()) {
            return flowOf(emptyList())
        }

        return identity.toSet().map(productRepository::observe).combine()
    }
}
