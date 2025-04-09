package com.maksimowiczm.foodyou.core.model

sealed interface FoodId {

    @JvmInline
    value class Product(val id: Long) : FoodId
}
