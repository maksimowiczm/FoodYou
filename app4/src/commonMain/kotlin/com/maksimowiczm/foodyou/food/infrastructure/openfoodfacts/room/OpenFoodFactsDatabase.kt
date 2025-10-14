package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [OpenFoodFactsProductEntity::class, OpenFoodFactsPagingKeyEntity::class],
    version = OpenFoodFactsDatabase.VERSION,
    exportSchema = false,
)
abstract class OpenFoodFactsDatabase : RoomDatabase() {
    abstract val dao: OpenFoodFactsDao

    companion object {
        const val VERSION = 1
    }
}
