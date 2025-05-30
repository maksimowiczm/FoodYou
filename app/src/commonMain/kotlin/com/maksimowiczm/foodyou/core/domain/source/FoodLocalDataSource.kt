package com.maksimowiczm.foodyou.core.domain.source

import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import kotlinx.coroutines.flow.Flow

interface FoodLocalDataSource {
    fun queryFood(query: String?, limit: Int = 100, offset: Int = 0): Flow<List<FoodSearchEntity>>

    fun queryFoodByBarcode(
        barcode: String,
        limit: Int = 100,
        offset: Int = 0
    ): Flow<List<FoodSearchEntity>>
}
