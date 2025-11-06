package com.maksimowiczm.foodyou.food.domain

class FoodRecipeDto(
    val identity: LocalFoodRecipeIdentity,
    val name: FoodName,
    val note: FoodNote?,
    val image: FoodImage?,
    val source: FoodSource?,
    val isLiquid: Boolean,
    val servings: Int,
    val ingredients: List<FoodRecipeIngredientDto>,
)
