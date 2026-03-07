package com.maksimowiczm.foodyou.common.domain.food

/** Represents a volume measurement. */
sealed interface Volume {
    /** The volume in milliliters. */
    val milliliters: Double

    operator fun times(scalar: Double): Volume

    operator fun div(divisor: Double): Volume = times(1.0 / divisor)
}

data class Milliliters(override val milliliters: Double) : Volume {
    override fun times(scalar: Double): Volume = Milliliters(milliliters * scalar)
}

data class FluidOunces(val fluidOunces: Double) : Volume {
    override val milliliters: Double
        get() = fluidOunces * MILLILITERS_IN_FLUID_OUNCE

    override fun times(scalar: Double): Volume = FluidOunces(milliliters * scalar)

    private companion object {
        const val MILLILITERS_IN_FLUID_OUNCE = 29.5735
    }
}
