package com.maksimowiczm.foodyou.core.data.model.food

import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement

data class FoodSearchEntity(
    // Identity
    val productId: Long?,
    val recipeId: Long?,
    val epochDay: Int,
    val mealId: Long,

    // Food
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val packageWeight: Float?,
    val servingWeight: Float?,

    // Measurement
    val measurementId: Long?,
    override val measurement: Measurement,
    override val quantity: Float,

    // UI id for add food screen. UI id must be unique for each food entity.
    // Entity without measurement id must have same id as first entity with measurement id. Every
    // other entity must have other id.
    /**
     * @see [com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodScreen]
     */
    val uiId: String
) : EntityWithMeasurement
