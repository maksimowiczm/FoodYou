package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters.DataType

internal class FoodDataCentralTypeConverters {
    @ColumnTypeConverter
    fun fromDataTypeSet(dataTypes: Set<DataType>?): String? {
        return dataTypes?.map { it.ordinal }?.sorted()?.joinToString(",")
    }

    @ColumnTypeConverter
    fun toDataTypeSet(dataTypesString: String?): Set<DataType>? {
        if (dataTypesString == null) return null
        if (dataTypesString.isEmpty()) return emptySet()
        return dataTypesString.split(",").map { DataType.entries[it.toInt()] }.toSet()
    }

    @ColumnTypeConverter fun fromDataType(dataType: DataType): Int = dataType.ordinal

    @ColumnTypeConverter fun toDataType(ordinal: Int): DataType = DataType.entries[ordinal]
}
