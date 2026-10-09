package com.maksimowiczm.foodyou.foodyou3.domain.food

sealed interface FoodYou3FoodId {
    data class Product(val id: Long) : FoodYou3FoodId

    data class Recipe(val id: Long) : FoodYou3FoodId
}
