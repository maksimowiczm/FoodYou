package com.maksimowiczm.foodyou.userfood.infrastructure.room

import androidx.room.TypeConverter

internal class QuantityTypeConverter {
    @TypeConverter
    fun fromQuantityType(value: QuantityType): Int =
        when (value) {
            QuantityType.Weight -> WEIGHT
            QuantityType.Volume -> VOLUME
        }

    @TypeConverter
    fun toQuantityType(value: Int): QuantityType =
        when (value) {
            WEIGHT -> QuantityType.Weight
            VOLUME -> QuantityType.Volume
            else -> error("Unknown quantity type: $value")
        }

    private companion object {
        private const val WEIGHT = 0
        private const val VOLUME = 1
    }
}
