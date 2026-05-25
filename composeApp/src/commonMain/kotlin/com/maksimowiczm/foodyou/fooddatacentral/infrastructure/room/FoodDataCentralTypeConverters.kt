package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters.DataType

internal class FoodDataCentralTypeConverters {
    @TypeConverter
    fun fromDataTypeSet(dataTypes: Set<DataType>?): String? {
        return dataTypes?.map { it.ordinal }?.sorted()?.joinToString(",")
    }

    @TypeConverter
    fun toDataTypeSet(dataTypesString: String?): Set<DataType>? {
        if (dataTypesString == null) return null
        if (dataTypesString.isEmpty()) return emptySet()
        return dataTypesString.split(",").map { DataType.entries[it.toInt()] }.toSet()
    }

    @TypeConverter fun fromDataType(dataType: DataType): Int = dataType.ordinal

    @TypeConverter fun toDataType(ordinal: Int): DataType = DataType.entries[ordinal]
}
