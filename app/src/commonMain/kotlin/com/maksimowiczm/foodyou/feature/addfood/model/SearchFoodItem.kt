package com.maksimowiczm.foodyou.feature.addfood.model

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.Measurement.Gram
import com.maksimowiczm.foodyou.core.domain.model.Measurement.Serving
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight

@Immutable
internal data class SearchFoodItem(
    val foodId: FoodId,
    val name: String,
    val brand: String?,

    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,

    val packageWeight: PortionWeight.Package?,
    val servingWeight: PortionWeight.Serving?,

    val measurement: Measurement,
    val measurementId: MeasurementId?
) {
    val isSelected: Boolean
        get() = measurementId != null

    val weight: Float?
        get() = with(measurement) {
            when (this) {
                is Gram -> value
                is Measurement.Package -> packageWeight?.let { weight(packageWeight) }
                is Serving -> servingWeight?.let { weight(servingWeight) }
            }
        }
}
