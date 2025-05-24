package com.maksimowiczm.foodyou.core.data.model.diaryday

import com.maksimowiczm.foodyou.core.data.model.Minerals
import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.Vitamins
import com.maksimowiczm.foodyou.core.data.model.abstraction.EntityWithMeasurement
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement

interface DiaryDay : EntityWithMeasurement {
    // Date
    val epochDay: Long

    // Meal
    val mealId: Long
    val mealName: String

    // Food
    val productId: Long?
    val recipeId: Long?
    val foodName: String
    val foodBrand: String?

    val nutrients: Nutrients
    val vitamins: Vitamins
    val minerals: Minerals

    val packageWeight: Float?
    val servingWeight: Float?

    // Measurement
    val measurementId: Long
    override val measurement: Measurement
    override val quantity: Float
}
