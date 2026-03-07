package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDatabase
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.EnergyFormatConverter
import com.maksimowiczm.foodyou.account.infrastructure.room.FoodIdentityTypeConverter
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileFavoriteFoodEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.SettingsEntity
import com.maksimowiczm.foodyou.foodsearch.infrastructure.room.FoodSearchDatabase
import com.maksimowiczm.foodyou.foodsearch.infrastructure.room.SearchHistoryEntity

@Database(
    entities =
        [
            AccountEntity::class,
            ProfileEntity::class,
            ProfileFavoriteFoodEntity::class,
            SettingsEntity::class,
            SearchHistoryEntity::class,
        ],
    version = AppDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(EnergyFormatConverter::class, FoodIdentityTypeConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase(), AccountDatabase, FoodSearchDatabase {
    companion object {
        const val VERSION = 1

        fun Builder<AppDatabase>.buildDatabase(): AppDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
