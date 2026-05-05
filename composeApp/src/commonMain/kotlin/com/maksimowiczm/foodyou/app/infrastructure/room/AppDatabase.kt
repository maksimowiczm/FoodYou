package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.common.infrastructure.room.EnergyUnitConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.UuidConverter
import com.maksimowiczm.foodyou.search.infrastructure.QuantityTypeConverter
import com.maksimowiczm.foodyou.search.infrastructure.SearchDatabase
import com.maksimowiczm.foodyou.search.infrastructure.SearchEntity
import com.maksimowiczm.foodyou.search.infrastructure.SearchEntityFts
import com.maksimowiczm.foodyou.search.infrastructure.history.SearchHistoryEntity

@Database(
    entities = [SearchHistoryEntity::class, SearchEntityFts::class, SearchEntity::class],
    version = AppDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(EnergyUnitConverter::class, UuidConverter::class, QuantityTypeConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase(), SearchDatabase {
    companion object {
        const val VERSION = 1

        fun Builder<AppDatabase>.buildDatabase(): AppDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
