package com.maksimowiczm.foodyou.food.infrastructure.usda.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [FoodDataCentralProductEntity::class, FoodDataCentralPagingKeyEntity::class],
    version = FoodDataCentralDatabase.VERSION,
    exportSchema = false,
)
@ConstructedBy(FoodDataCentralDatabaseConstructor::class)
abstract class FoodDataCentralDatabase : RoomDatabase() {
    abstract val dao: FoodDataCentralDao

    companion object {
        const val VERSION = 1

        fun Builder<FoodDataCentralDatabase>.buildDatabase(): FoodDataCentralDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
expect object FoodDataCentralDatabaseConstructor :
    RoomDatabaseConstructor<FoodDataCentralDatabase> {
    override fun initialize(): FoodDataCentralDatabase
}
