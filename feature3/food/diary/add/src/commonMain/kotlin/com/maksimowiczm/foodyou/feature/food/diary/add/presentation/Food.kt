package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipeIngredient
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType

val Food.possibleMeasurementTypes: List<MeasurementType>
    get() =
        MeasurementType.entries.filter { type ->
            when (type) {
                MeasurementType.Gram -> !isLiquid
                MeasurementType.Milliliter -> isLiquid
                MeasurementType.Package -> totalWeight != null
                MeasurementType.Serving -> servingWeight != null
            }
        }

val Food.defaultMeasurement: Measurement
    get() =
        when {
            servingWeight != null -> Measurement.Serving(1.0)
            totalWeight != null -> Measurement.Package(1.0)
            isLiquid -> Measurement.Milliliter(100.0)
            else -> Measurement.Gram(100.0)
        }

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
