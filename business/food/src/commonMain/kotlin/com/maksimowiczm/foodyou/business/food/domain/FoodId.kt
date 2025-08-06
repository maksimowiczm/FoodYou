package com.maksimowiczm.foodyou.business.food.domain

sealed interface FoodId {

    data class Product(val id: Long) : FoodId

    data class Recipe(val id: Long) : FoodId
}
