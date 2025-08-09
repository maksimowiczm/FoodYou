package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

@Immutable
internal data class RecipeModel(
    override val foodId: FoodId.Recipe,
    override val name: String,
    override val nutritionFacts: NutritionFacts,
    override val isLiquid: Boolean,
    override val note: String?,
    val totalWeight: Double,
    val servings: Int,
    private val ingredients: List<IngredientModel>,
    val allIngredients: List<Triple<FoodId, String, NutritionFacts>>,
) : FoodModel {
    constructor(
        recipe: Recipe
    ) : this(
        foodId = recipe.id,
        name = recipe.headline,
        nutritionFacts = recipe.nutritionFacts,
        isLiquid = recipe.isLiquid,
        note = recipe.note,
        totalWeight = recipe.totalWeight,
        servings = recipe.servings,
        ingredients = buildIngredients(recipe),
        allIngredients =
            recipe.allIngredients().map { Triple(it.id, it.headline, it.nutritionFacts) },
    )

    fun measuredIngredients(weight: Double): List<IngredientModel> {
        val fraction = weight / totalWeight

        return ingredients.map { ingredient ->
            val measurement = ingredient.measurement
            val newMeasurement =
                when (measurement) {
                    is Measurement.Gram -> Measurement.Gram(measurement.value * fraction)

                    is Measurement.Milliliter ->
                        Measurement.Milliliter(measurement.value * fraction)

                    is Measurement.Package -> Measurement.Package(measurement.quantity * fraction)

                    is Measurement.Serving -> Measurement.Serving(measurement.quantity * fraction)
                }

            ingredient.copy(
                measurement = newMeasurement,
                nutritionFacts = ingredient.nutritionFacts?.times(fraction),
            )
        }
    }

    override fun weight(measurement: Measurement): Double =
        when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Milliliter -> measurement.value
            is Measurement.Package -> totalWeight * measurement.quantity
            is Measurement.Serving -> totalWeight * measurement.quantity / servings
        }
}

internal fun buildIngredients(recipe: Recipe): List<IngredientModel> =
    recipe.ingredients.map(::IngredientModel).sortedBy { it.name.lowercase() }
