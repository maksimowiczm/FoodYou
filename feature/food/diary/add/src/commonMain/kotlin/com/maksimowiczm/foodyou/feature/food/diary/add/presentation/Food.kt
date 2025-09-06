package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import com.maksimowiczm.foodyou.food.domain.entity.Food
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.food.domain.entity.Recipe
import com.maksimowiczm.foodyou.food.domain.entity.RecipeIngredient
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFood
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodProduct
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipe
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipeIngredient

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
        source = source,
        note = note,
    )

private fun Recipe.toDiaryRecipe(): DiaryFoodRecipe =
    DiaryFoodRecipe(
        name = headline,
        servings = servings,
        ingredients = ingredients.map { it.toDiaryRecipeIngredient() },
        isLiquid = isLiquid,
        note = note,
    )

private fun RecipeIngredient.toDiaryRecipeIngredient(): DiaryFoodRecipeIngredient =
    DiaryFoodRecipeIngredient(food = food.toDiaryFood(), measurement = measurement)
