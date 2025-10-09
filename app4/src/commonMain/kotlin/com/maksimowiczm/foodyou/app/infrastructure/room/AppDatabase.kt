package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountDatabase
import com.maksimowiczm.foodyou.account.infrastructure.room.AccountEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.ProfileEntity
import com.maksimowiczm.foodyou.account.infrastructure.room.SettingsEntity

@Database(
    entities = [AccountEntity::class, ProfileEntity::class, SettingsEntity::class],
    version = AppDatabase.VERSION,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase(), AccountDatabase {
    companion object {
        const val VERSION = 1

        fun Builder<AppDatabase>.buildDatabase(): AppDatabase = build()
    }
}
