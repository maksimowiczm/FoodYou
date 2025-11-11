package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.Quantity

sealed interface FoodRecipeIngredientDto {
    class Product(val data: FoodProductDto, val quantity: Quantity) : FoodRecipeIngredientDto

    class Recipe(val data: FoodRecipeDto, val quantity: Quantity) : FoodRecipeIngredientDto
}
