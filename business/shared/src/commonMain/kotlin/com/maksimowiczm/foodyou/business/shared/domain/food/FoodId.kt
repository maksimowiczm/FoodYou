package com.maksimowiczm.foodyou.business.shared.domain.food

sealed interface FoodId {

    data class Product(val id: Long) : FoodId

    data class Recipe(val id: Long) : FoodId
}
