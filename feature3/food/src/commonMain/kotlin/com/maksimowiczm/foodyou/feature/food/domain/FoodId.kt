package com.maksimowiczm.foodyou.feature.food.domain

import kotlin.jvm.JvmInline

sealed interface FoodId {

    @JvmInline
    value class Product(val id: Long) : FoodId

    @JvmInline
    value class Recipe(val id: Long) : FoodId
}
