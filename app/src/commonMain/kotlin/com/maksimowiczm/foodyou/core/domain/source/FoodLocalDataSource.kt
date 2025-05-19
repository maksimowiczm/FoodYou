package com.maksimowiczm.foodyou.core.domain.source

import androidx.paging.PagingSource
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity

interface FoodLocalDataSource {
    fun queryFood(query: String?, mealId: Long, epochDay: Int): PagingSource<Int, FoodSearchEntity>

    fun queryFoodByBarcode(
        barcode: String,
        mealId: Long,
        epochDay: Int
    ): PagingSource<Int, FoodSearchEntity>
}
