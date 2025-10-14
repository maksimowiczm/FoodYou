package com.maksimowiczm.foodyou.common.domain

/** Represents a weight measurement. */
sealed interface Weight {
    /** The weight in grams. */
    val grams: Double

    operator fun times(scalar: Double): Weight
}

data class Grams(override val grams: Double) : Weight {
    override fun times(scalar: Double): Weight = Grams(grams * scalar)
}

data class Ounces(val ounces: Double) : Weight {
    override val grams: Double
        get() = ounces * GRAMS_IN_OUNCE

    override fun times(scalar: Double): Weight = Ounces(ounces * scalar)

    private companion object {
        const val GRAMS_IN_OUNCE = 28.3495
    }
}
