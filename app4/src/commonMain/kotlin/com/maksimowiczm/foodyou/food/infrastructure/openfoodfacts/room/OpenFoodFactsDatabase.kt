package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [OpenFoodFactsProductEntity::class, OpenFoodFactsPagingKeyEntity::class],
    version = OpenFoodFactsDatabase.VERSION,
    exportSchema = false,
)
@ConstructedBy(OpenFoodFactsDatabaseConstructor::class)
abstract class OpenFoodFactsDatabase : RoomDatabase() {
    abstract val dao: OpenFoodFactsDao

    companion object {
        const val VERSION = 1
    }
}

@Suppress("KotlinNoActualForExpect")
expect object OpenFoodFactsDatabaseConstructor : RoomDatabaseConstructor<OpenFoodFactsDatabase> {
    override fun initialize(): OpenFoodFactsDatabase
}
