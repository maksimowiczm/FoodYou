package com.maksimowiczm.foodyou.recipe.domain

import kotlin.jvm.JvmInline

/**
 * Reference to food from any bounded context.
 *
 * This allows recipes to include:
 * - User-created food products (UserFood context)
 * - External food products (FoodDataCentral, OpenFoodFacts contexts)
 * - Other recipes (Recipe context)
 */
sealed interface FoodReference {
    val foodId: String

    /**
     * Reference to a user-created food product.
     *
     * @property foodId UUID of the UserFoodProduct
     */
    @JvmInline value class UserFood(override val foodId: String) : FoodReference

    /**
     * Reference to a food from FoodDataCentral.
     *
     * @property foodId FDC ID of the food
     */
    @JvmInline value class FoodDataCentral(override val foodId: String) : FoodReference

    /**
     * Reference to a food from OpenFoodFacts.
     *
     * @property foodId Barcode or product ID
     */
    @JvmInline value class OpenFoodFacts(override val foodId: String) : FoodReference

    /**
     * Reference to another recipe (allows nested recipes).
     *
     * @property foodId UUID of the Recipe
     */
    @JvmInline value class Recipe(override val foodId: String) : FoodReference
}
