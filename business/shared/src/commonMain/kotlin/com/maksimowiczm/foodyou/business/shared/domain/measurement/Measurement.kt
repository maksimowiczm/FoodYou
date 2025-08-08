package com.maksimowiczm.foodyou.business.shared.domain.measurement

import kotlin.jvm.JvmInline

sealed interface Measurement {

    @JvmInline value class Gram(val value: Double) : Measurement

    @JvmInline value class Milliliter(val value: Double) : Measurement

    @JvmInline
    value class Package(val quantity: Double) : Measurement {

        /** Calculates the weight of the package based on the given package weight. */
        fun weight(packageWeight: Double): Double = packageWeight * quantity
    }

    @JvmInline
    value class Serving(val quantity: Double) : Measurement {

        /** Calculates the weight of the serving based on the given serving weight. */
        fun weight(servingWeight: Double): Double = servingWeight * quantity
    }

    companion object
}
