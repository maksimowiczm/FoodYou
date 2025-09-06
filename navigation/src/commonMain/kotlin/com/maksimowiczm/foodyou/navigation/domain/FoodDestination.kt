package com.maksimowiczm.foodyou.navigation.domain

import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateProductDestination(val productId: Long) : NavigationDestination {
    constructor(foodId: FoodId.Product) : this(foodId.id)

    val foodId: FoodId.Product
        get() = FoodId.Product(productId)
}

@Serializable
internal data class UpdateRecipeDestination(val recipeId: Long) : NavigationDestination {
    constructor(foodId: FoodId.Recipe) : this(foodId.id)

    val foodId: FoodId.Recipe
        get() = FoodId.Recipe(recipeId)
}

@Serializable
internal data class CreateProductDestination(val url: String?) : NavigationDestination
