package com.maksimowiczm.foodyou.feature.diary.core.data.measurement

import com.maksimowiczm.foodyou.feature.diary.core.data.food.PortionWeight

sealed interface Measurement {

    @JvmInline
    value class Gram(val value: Float) : Measurement

    @JvmInline
    value class Package(val quantity: Float) : Measurement {
        fun weight(unit: PortionWeight.Package) = unit.weight * quantity
    }

    @JvmInline
    value class Serving(val quantity: Float) : Measurement {
        fun weight(unit: PortionWeight.Serving) = unit.weight * quantity
    }
}
