package com.maksimowiczm.foodyou.food.infrastructure.user.room

import androidx.room.TypeConverter

class QuantityEntityConverter {
    @TypeConverter fun fromQuantityType(value: QuantityType): Int = value.ordinal

    @TypeConverter fun toQuantityType(value: Int): QuantityType = QuantityType.entries[value]

    @TypeConverter fun fromUnit(value: MeasurementUnit): Int = value.ordinal

    @TypeConverter fun toUnit(value: Int): MeasurementUnit = MeasurementUnit.entries[value]
}
