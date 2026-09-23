package com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room

import androidx.room3.*

@Database(
    entities = [FoodDataCentralProductEntity::class, FoodDataCentralPagingKeyEntity::class],
    version = FoodDataCentralDatabase.VERSION,
    exportSchema = false,
)
@ColumnTypeConverters(FoodDataCentralTypeConverters::class)
@ConstructedBy(FoodDataCentralDatabaseConstructor::class)
internal abstract class FoodDataCentralDatabase : RoomDatabase() {
    abstract val dao: FoodDataCentralDao

    companion object {
        const val VERSION = 1

        fun Builder<FoodDataCentralDatabase>.buildDatabase(): FoodDataCentralDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object FoodDataCentralDatabaseConstructor :
    RoomDatabaseConstructor<FoodDataCentralDatabase> {
    override fun initialize(): FoodDataCentralDatabase
}
