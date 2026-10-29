package com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor

@Database(
    entities =
        [
            SwissFoodCompositionEntity::class,
            SwissFoodCompositionEntityFts::class,
        ],
    version = 1,
    exportSchema = false,
)
@ConstructedBy(SwissFoodCompositionDatabaseConstructor::class)
abstract class SwissFoodCompositionDatabase : RoomDatabase() {
    abstract val swissFoodCompositionDao: SwissFoodCompositionDao

    companion object {
        const val VERSION = 1

        fun Builder<SwissFoodCompositionDatabase>.buildDatabase(): SwissFoodCompositionDatabase =
            build()
    }
}

@Suppress("KotlinNoActualForExpect")
expect object SwissFoodCompositionDatabaseConstructor :
    RoomDatabaseConstructor<SwissFoodCompositionDatabase> {
    override fun initialize(): SwissFoodCompositionDatabase
}
