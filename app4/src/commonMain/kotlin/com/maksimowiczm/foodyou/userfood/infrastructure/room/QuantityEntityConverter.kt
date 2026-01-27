package com.maksimowiczm.foodyou.userfood.infrastructure.room

import androidx.room.TypeConverter

internal class QuantityEntityConverter {
    @TypeConverter fun fromQuantityType(value: QuantityType): Int = value.ordinal

    @TypeConverter fun toQuantityType(value: Int): QuantityType = QuantityType.entries[value]

    @TypeConverter fun fromUnit(value: MeasurementUnit): Int = value.ordinal

    @TypeConverter fun toUnit(value: Int): MeasurementUnit = MeasurementUnit.entries[value]
}
