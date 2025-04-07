package com.maksimowiczm.foodyou.feature.diary.core.data.food

sealed interface FoodId {

    @JvmInline
    value class Product(val id: Long) : FoodId
}
