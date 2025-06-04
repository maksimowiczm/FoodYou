package com.maksimowiczm.foodyou.core.model

sealed interface Measurement {

    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    fun weight(food: Food): Float? = when (this) {
        is Gram -> value
        is Package -> food.totalWeight?.let { weight(it) }
        is Serving -> food.servingWeight?.let { weight(it) }
    }

    @JvmInline
    value class Gram(val value: Float) : Measurement

    @JvmInline
    value class Package(val quantity: Float) : Measurement {
        fun weight(packageWeight: Float) = packageWeight * quantity
    }

    @JvmInline
    value class Serving(val quantity: Float) : Measurement {
        fun weight(servingWeight: Float) = servingWeight * quantity
    }

    companion object {
        fun defaultForFood(food: Food): Measurement = when {
            food.servingWeight != null -> Serving(1f)
            food.totalWeight != null -> Package(1f)
            else -> Gram(100f)
        }
    }
}
