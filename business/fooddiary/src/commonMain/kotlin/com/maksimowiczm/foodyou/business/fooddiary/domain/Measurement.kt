package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement

fun Measurement.weight(food: DiaryFood): Double =
    when (this) {
        is Measurement.Gram -> value
        is Measurement.Milliliter -> value
        is Measurement.Package ->
            food.totalWeight?.times(quantity)
                ?: error("Total weight is not defined for package measurement")

        is Measurement.Serving ->
            food.servingWeight?.times(quantity)
                ?: error("Serving weight is not defined for serving measurement")
    }
