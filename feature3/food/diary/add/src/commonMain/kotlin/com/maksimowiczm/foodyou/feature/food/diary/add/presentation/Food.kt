package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipeIngredient

fun Food.toDiaryFood(): DiaryFood =
    when (this) {
        is Product -> toDiaryProduct()
        is Recipe -> toDiaryRecipe()
    }

private fun Product.toDiaryProduct(): DiaryFoodProduct =
    DiaryFoodProduct(
        name = headline,
        nutritionFacts = nutritionFacts,
        servingWeight = servingWeight,
        totalWeight = totalWeight,
        isLiquid = isLiquid,
    )

private fun Recipe.toDiaryRecipe(): DiaryFoodRecipe =
    DiaryFoodRecipe(
        name = headline,
        servings = servings,
        ingredients = ingredients.map { it.toDiaryRecipeIngredient() },
        isLiquid = isLiquid,
    )

private fun RecipeIngredient.toDiaryRecipeIngredient(): DiaryFoodRecipeIngredient =
    DiaryFoodRecipeIngredient(food = food.toDiaryFood(), measurement = measurement)
