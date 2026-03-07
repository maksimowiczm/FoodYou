package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

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
internal abstract class OpenFoodFactsDatabase : RoomDatabase() {
    abstract val dao: OpenFoodFactsDao

    companion object {
        const val VERSION = 1

        fun Builder<OpenFoodFactsDatabase>.buildDatabase(): OpenFoodFactsDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object OpenFoodFactsDatabaseConstructor :
    RoomDatabaseConstructor<OpenFoodFactsDatabase> {
    override fun initialize(): OpenFoodFactsDatabase
}
