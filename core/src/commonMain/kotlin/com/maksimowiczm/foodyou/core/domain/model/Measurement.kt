package com.maksimowiczm.foodyou.core.domain.model

sealed interface Measurement {

    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    fun weight(food: Food): Float? = when (this) {
        is Gram -> value
        is Package -> food.packageWeight?.let { weight(it) }
        is Serving -> food.servingWeight?.let { weight(it) }
    }

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

    companion object
}
