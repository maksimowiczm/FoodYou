package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TypeConverter

class MeasurementUnitConverter {
    @TypeConverter
    fun fromUnit(value: MeasurementUnit): Int =
        when (value) {
            MeasurementUnit.Grams -> GRAMS
            MeasurementUnit.Ounces -> OUNCES
            MeasurementUnit.Milliliters -> MILLILITERS
            MeasurementUnit.FluidOunces -> FLUID_OUNCES
        }

    @TypeConverter
    fun toUnit(value: Int): MeasurementUnit =
        when (value) {
            GRAMS -> MeasurementUnit.Grams
            OUNCES -> MeasurementUnit.Ounces
            MILLILITERS -> MeasurementUnit.Milliliters
            FLUID_OUNCES -> MeasurementUnit.FluidOunces
            else -> error("Unknown measurement unit: $value")
        }

    private companion object {
        private const val GRAMS = 0
        private const val OUNCES = 1
        private const val MILLILITERS = 2
        private const val FLUID_OUNCES = 3
    }
}
