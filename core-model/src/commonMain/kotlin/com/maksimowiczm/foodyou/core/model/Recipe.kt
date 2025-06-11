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
     *
     * The returned list contains pairs of [Food] and nullable [Measurement].
     * If an ingredient does not have a valid measurement, it will be paired with `null`.
     */
    fun measuredIngredients(measurement: Measurement): List<Pair<Food, Measurement?>> {
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
     *
     * The returned list contains pairs of [Food] and nullable [Measurement].
     * If an ingredient does not have a valid measurement, it will be paired with `null`.
     */
    fun measuredIngredients(weight: Float): List<Pair<Food, Measurement?>> {
        val fractions = ingredients
            .mapNotNull { it.weight?.let { weight -> it.food.id to weight } }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, weights) -> weights.sum() / totalWeight }

        val measurements = ingredients.map { ingredient ->
            val fraction = fractions[ingredient.food.id]

            if (fraction == null) {
                return@map ingredient.food to null
            }

            val ingredientWeight = weight * fraction
            val measurement = when (ingredient.measurement) {
                is Measurement.Gram -> Measurement.Gram(ingredientWeight)

                is Measurement.Package -> {
                    val packageWeight = ingredient.food.totalWeight

                    if (packageWeight == null) {
                        return@map ingredient.food to null
                    }

                    val quantity = ingredientWeight / packageWeight
                    Measurement.Package(quantity)
                }

                is Measurement.Serving -> {
                    val servingWeight = ingredient.food.servingWeight

                    if (servingWeight == null) {
                        return@map ingredient.food to null
                    }

                    val quantity = ingredientWeight / servingWeight
                    Measurement.Serving(quantity)
                }
            }

            ingredient.food to measurement
        }

        return measurements
    }
}
