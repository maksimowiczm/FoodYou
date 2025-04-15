package com.maksimowiczm.foodyou.core.domain.source

import androidx.paging.PagingSource
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity

interface FoodLocalDataSource {
    fun queryFood(
        query1: String?,
        query2: String?,
        query3: String?,
        query4: String?,
        query5: String?,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>

    fun queryFoodByBarcode(
        barcode: String,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>
}
