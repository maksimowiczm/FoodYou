package com.maksimowiczm.foodyou.feature.diary.database

import androidx.room.Embedded

data class MealProductWithProduct(
    @Embedded
    val mealProduct: MealProductEntity,

    @Embedded
    val product: ProductEntity
)
