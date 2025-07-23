package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.TypeConverter

@Suppress("unused")
class ProductEventTypeConverter {
    @TypeConverter
    fun fromProductEventType(type: ProductEventType): Int = type.ordinal

    @TypeConverter
    fun toProductEventType(type: Int): ProductEventType = ProductEventType.entries[type]
}
