package com.maksimowiczm.foodyou.food.domain

import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Quantity

/**
 * A representation of a food recipe with its ingredients represented only by their identities and
 * quantities.
 */
class LazyFoodRecipeDto(
    override val identity: LocalFoodRecipeIdentity,
    val name: FoodName,
    val note: FoodNote?,
    val image: FoodImage?,
    val source: FoodSource?,
    val servings: Int,
    val finalWeight: Grams,
    val ingredients: List<Pair<FoodIdentity, Quantity>>,
) : Food

/** A simple representation of a food recipe, without its ingredients. */
class SimpleFoodRecipeDto(
    override val identity: FoodIdentity,
    val name: FoodName,
    val image: FoodImage?,
    val nutritionFacts: NutritionFacts,
) : Food
