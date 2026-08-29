package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room

import androidx.room3.*

@Database(
    entities =
        [
            OpenFoodFactsProductEntity::class,
            OpenFoodFactsPagingKeyV1Entity::class,
            OpenFoodFactsPagingKeySearchALiciousEntity::class,
        ],
    version = OpenFoodFactsDatabase.VERSION,
    exportSchema = false,
)
@ConstructedBy(OpenFoodFactsDatabaseConstructor::class)
internal abstract class OpenFoodFactsDatabase : RoomDatabase() {
    abstract val productDao: OpenFoodFactsProductDao
    abstract val pagingKeyV1Dao: OpenFoodFactsPagingKeyV1Dao
    abstract val pagingKeySearchALiciousDao: OpenFoodFactsPagingKeySearchALiciousDao

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
