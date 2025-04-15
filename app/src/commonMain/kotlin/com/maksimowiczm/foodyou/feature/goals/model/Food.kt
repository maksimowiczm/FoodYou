package com.maksimowiczm.foodyou.feature.goals.model

import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.Nutrients
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight

internal data class Food(
    val foodId: FoodId,
    val name: String,
    val packageWeight: PortionWeight.Package?,
    val servingWeight: PortionWeight.Serving?,
    val nutrients: Nutrients,
    val measurement: Measurement
) {
    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    val weight: Float? = when (val measurement = measurement) {
        is Measurement.Gram -> measurement.value
        is Measurement.Package -> packageWeight?.weight?.let { it * measurement.quantity }
        is Measurement.Serving -> servingWeight?.weight?.let { it * measurement.quantity }
    }

    /**
     * Nutrients for a given [weight]. If weight is null then nutrients are invalid.
     */
    val realNutrients: Nutrients?
        get() = weight?.let { nutrients * (weight / 100f) }
}
