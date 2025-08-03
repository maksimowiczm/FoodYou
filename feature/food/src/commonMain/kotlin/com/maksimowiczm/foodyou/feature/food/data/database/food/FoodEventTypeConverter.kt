package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.TypeConverter

@Suppress("unused")
class FoodEventTypeConverter {
    @TypeConverter
    fun fromFoodEventType(type: FoodEventType): Int = when (type) {
        FoodEventType.Created -> FoodEventTypeSQLConstants.CREATED
        FoodEventType.Downloaded -> FoodEventTypeSQLConstants.DOWNLOADED
        FoodEventType.Imported -> FoodEventTypeSQLConstants.IMPORTED
        FoodEventType.Edited -> FoodEventTypeSQLConstants.EDITED
        FoodEventType.Measured -> FoodEventTypeSQLConstants.MEASURED
        FoodEventType.ImportedFromFoodYou2 -> FoodEventTypeSQLConstants.IMPORTED_FROM_FOOD_YOU_2
    }

    @TypeConverter
    fun toFoodEventType(type: Int): FoodEventType = when (type) {
        FoodEventTypeSQLConstants.CREATED -> FoodEventType.Created
        FoodEventTypeSQLConstants.DOWNLOADED -> FoodEventType.Downloaded
        FoodEventTypeSQLConstants.IMPORTED -> FoodEventType.Imported
        FoodEventTypeSQLConstants.EDITED -> FoodEventType.Edited
        FoodEventTypeSQLConstants.MEASURED -> FoodEventType.Measured
        FoodEventTypeSQLConstants.IMPORTED_FROM_FOOD_YOU_2 -> FoodEventType.ImportedFromFoodYou2
        else -> error("Unknown food event type: $type")
    }
}

object FoodEventTypeSQLConstants {
    const val CREATED = 0
    const val DOWNLOADED = 1
    const val IMPORTED = 2
    const val EDITED = 3
    const val MEASURED = 4
    const val IMPORTED_FROM_FOOD_YOU_2 = 5
}
