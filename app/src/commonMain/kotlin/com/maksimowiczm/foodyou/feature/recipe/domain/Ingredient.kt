package com.maksimowiczm.foodyou.feature.recipe.domain

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.NutritionFacts
import com.maksimowiczm.foodyou.core.model.Product as DomainProduct
import com.maksimowiczm.foodyou.core.model.Recipe as DomainRecipe

@Immutable
internal sealed interface Ingredient {

    val uniqueId: String

    val food: Food
    val measurement: Measurement

    val weight: Float?
        get() = measurement.weight(food)

    val proteins: Float?
        get() = weight?.let { food.nutritionFacts.proteins.value * it / 100f }

    val carbohydrates: Float?
        get() = weight?.let { food.nutritionFacts.carbohydrates.value * it / 100f }

    val fats: Float?
        get() = weight?.let { food.nutritionFacts.fats.value * it / 100f }

    val calories: Float?
        get() = weight?.let { food.nutritionFacts.calories.value * it / 100f }

    @Immutable
    data class Product(
        override val uniqueId: String,
        override val food: DomainProduct,
        override val measurement: Measurement
    ) : Ingredient

    @Immutable
    data class Recipe(
        override val uniqueId: String,
        override val food: DomainRecipe,
        override val measurement: Measurement
    ) : Ingredient
}

internal fun Iterable<Ingredient>.nutritionFacts() = fold(NutritionFacts.Empty) { acc, ingredient ->
    acc + ingredient.food.nutritionFacts * (ingredient.weight ?: 0f) / 100f
}
