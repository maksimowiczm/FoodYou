package com.maksimowiczm.foodyou.userfood.domain.recipe

import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.userfood.domain.FoodNote

/**
 * Recipe aggregate root.
 *
 * A recipe is a collection of food ingredients with instructions. Recipes can reference food from
 * any bounded context and can be nested.
 *
 * @property identity Unique identifier for the recipe
 * @property name Recipe name
 * @property servings Number of servings this recipe makes
 * @property image Optional recipe image
 * @property note Optional note for the recipe
 * @property finalWeight Optional final weight of the recipe, should be used to calculate final
 *   nutritional values
 * @property ingredients List of ingredients with quantities
 */
data class Recipe(
    val identity: RecipeIdentity,
    val name: RecipeName,
    val servings: Double,
    val image: Image.Local?,
    val note: FoodNote?,
    val finalWeight: Double?,
    val ingredients: List<RecipeIngredient>,
) {
    init {
        require(ingredients.isNotEmpty()) { "Recipe must have at least one ingredient" }
        require(servings > 0) { "Recipe must have a positive number of servings" }
    }
}
