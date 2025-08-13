package com.maksimowiczm.foodyou.feature.food.shared.presentation

import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType

val Food.possibleMeasurementTypes: List<MeasurementType>
    get() =
        MeasurementType.entries.filter { type ->
            when (type) {
                MeasurementType.Gram -> !isLiquid
                MeasurementType.Milliliter -> isLiquid
                MeasurementType.Package -> totalWeight != null
                MeasurementType.Serving -> servingWeight != null
            }
        }

val Food.defaultMeasurement: Measurement
    get() =
        when {
            servingWeight != null -> Measurement.Serving(1.0)
            totalWeight != null -> Measurement.Package(1.0)
            isLiquid -> Measurement.Milliliter(100.0)
            else -> Measurement.Gram(100.0)
        }
