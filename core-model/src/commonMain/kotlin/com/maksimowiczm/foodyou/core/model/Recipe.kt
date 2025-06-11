package com.maksimowiczm.foodyou.core.model

data class Recipe(
    override val id: FoodId.Recipe,
    val name: String,
    val servings: Int,
    val ingredients: List<RecipeIngredient>
) : Food {

    override val headline: String
        get() = name

    override val totalWeight: Float
        get() {
            val weight = ingredients.mapNotNull { it.weight }.fold(0f) { acc, ingredient ->
                acc + ingredient
            }
            return weight
        }

    override val servingWeight: Float
        get() = totalWeight / servings

    override val nutritionFacts: NutritionFacts
        get() = if (ingredients.isEmpty()) {
            NutritionFacts.Empty
        } else {
            val sum = ingredients
                .map { it.food.nutritionFacts * (it.weight ?: 0f) }
                .sum()

            if (sum.isEmpty) {
                NutritionFacts.Empty
            } else {
                sum / totalWeight
            }
        }

    /**
     * Returns a flat list of all ingredients in the recipe.
     */
    fun flatIngredients(): List<Food> = ingredients.flatMap { ingredient ->
        val food = ingredient.food
        if (food is Recipe) {
            listOf(food) + food.flatIngredients()
        } else {
            listOf(food)
        }
    }.distinct()

    /**
     * Returns a list of ingredients with their measurements based on the given [measurement].
     * The weight is calculated based on the total weight of the recipe and the serving size.
     */
    fun measuredIngredients(measurement: Measurement): List<RecipeIngredient> {
        val weight = when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> measurement.quantity * totalWeight
            is Measurement.Serving -> measurement.quantity * servingWeight
        }

        return measuredIngredients(weight)
    }

    /**
     * Returns a list of ingredients with their measurements based on the given [weight].
     * The weight is calculated based on the total weight of the recipe.
     */
    fun measuredIngredients(weight: Float): List<RecipeIngredient> {
        val fractions = ingredients
            .mapNotNull { it.weight?.let { weight -> it.food.id to weight } }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, weights) -> weights.sum() / totalWeight }

        val measurements = ingredients.map { ingredient ->
            val fraction = fractions[ingredient.food.id]

            checkNotNull(fraction) {
                "Fraction for ingredient ${ingredient.food} in recipe $this is null"
            }

            val ingredientWeight = weight * fraction
            val measurement = when (ingredient.measurement) {
                is Measurement.Gram -> Measurement.Gram(ingredientWeight)

                is Measurement.Package -> {
                    val packageWeight = ingredient.food.totalWeight
                    checkNotNull(packageWeight) {
                        "No total weight for ingredient: ${ingredient.food}"
                    }

                    val quantity = ingredientWeight / packageWeight
                    Measurement.Package(quantity)
                }

                is Measurement.Serving -> {
                    val servingWeight = ingredient.food.servingWeight
                    checkNotNull(servingWeight) {
                        "No serving weight for ingredient: ${ingredient.food}"
                    }

                    val quantity = ingredientWeight / servingWeight
                    Measurement.Serving(quantity)
                }
            }

            RecipeIngredient(
                food = ingredient.food,
                measurement = measurement
            )
        }

        return measurements
    }
}
