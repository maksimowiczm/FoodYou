package com.maksimowiczm.foodyou.userfood.infrastructure.recipe.room

import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnit

/**
 * Represents a quantity for recipe ingredients.
 *
 * Supports absolute quantities (weight/volume) as well as package and serving quantities.
 *
 * @property type The type of quantity (Weight, Volume, Package, Serving)
 * @property amount The numeric amount
 * @property unit The measurement unit (only used for Weight and Volume types, null for Package and
 *   Serving)
 */
internal data class RecipeQuantityEntity(
    val type: RecipeQuantityType,
    val amount: Double,
    val unit: MeasurementUnit?,
)
