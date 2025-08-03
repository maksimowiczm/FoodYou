package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.Food
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

val Food.defaultMeasurement: Measurement
    get() = when {
        servingWeight != null -> Measurement.Serving(1f)
        totalWeight != null -> Measurement.Package(1f)
        isLiquid -> Measurement.Milliliter(100f)
        else -> Measurement.Gram(100f)
    }
