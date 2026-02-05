package com.maksimowiczm.foodyou.recipe.domain

import com.maksimowiczm.foodyou.common.domain.food.Quantity

/**
 * An ingredient in a recipe with quantity.
 *
 * @property foodReference Reference to the food item
 * @property quantity Amount of the food needed
 */
data class RecipeIngredient(val foodReference: FoodReference, val quantity: Quantity)
