package com.maksimowiczm.foodyou.core.model

sealed interface Measurement {

    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    fun weight(food: Food): Float? = when (this) {
        is Gram -> value
        is Package -> when (food) {
            is Product -> food.packageWeight?.let { weight(food.packageWeight) }
        }

        is Serving -> when (food) {
            is Product -> food.servingWeight?.let { weight(food.servingWeight) }
        }
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
