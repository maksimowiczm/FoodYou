package com.maksimowiczm.foodyou.core.model

sealed interface Measurement {

    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    fun weight(food: Food): Float? = when (this) {
        is Gram -> value
        is Milliliter -> value
        is Package -> food.totalWeight?.let { weight(it) }
        is Serving -> food.servingWeight?.let { weight(it) }
    }

    @JvmInline
    value class Gram(val value: Float) : Measurement

    @JvmInline
    value class Milliliter(val value: Float) : Measurement

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
            food.isLiquid -> Milliliter(100f)
            else -> Gram(100f)
        }

        val comparator: Comparator<Measurement> = Comparator { a, b ->
            when {
                a is Gram && b is Gram -> a.value.compareTo(b.value)
                a is Milliliter && b is Milliliter -> a.value.compareTo(b.value)
                a is Package && b is Package -> a.quantity.compareTo(b.quantity)
                a is Serving && b is Serving -> a.quantity.compareTo(b.quantity)
                else -> 0
            }
        }
    }
}
