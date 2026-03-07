package com.maksimowiczm.foodyou.userfood.domain.recipe

import com.maksimowiczm.foodyou.common.domain.food.Quantity

/**
 * An ingredient in a recipe with quantity.
 *
 * @property foodReference Reference to the food item
 * @property quantity Amount of the food needed
 */
data class UserRecipeIngredient(val foodReference: FoodReference, val quantity: Quantity)
