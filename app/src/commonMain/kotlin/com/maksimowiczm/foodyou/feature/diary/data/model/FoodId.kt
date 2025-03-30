package com.maksimowiczm.foodyou.feature.diary.data.model

sealed interface FoodId {
    data class Product(val productId: Long) : FoodId
}
