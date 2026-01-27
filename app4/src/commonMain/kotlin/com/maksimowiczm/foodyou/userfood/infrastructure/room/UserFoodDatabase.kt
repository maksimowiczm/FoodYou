package com.maksimowiczm.foodyou.userfood.infrastructure.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(
    entities = [UserFoodEntity::class, UserFoodFts::class],
    version = UserFoodDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(QuantityEntityConverter::class)
@ConstructedBy(UserFoodDatabaseConstructor::class)
internal abstract class UserFoodDatabase : RoomDatabase() {
    abstract val dao: UserFoodDao

    companion object {
        const val VERSION = 1

        fun Builder<UserFoodDatabase>.buildDatabase(): UserFoodDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object UserFoodDatabaseConstructor : RoomDatabaseConstructor<UserFoodDatabase> {
    override fun initialize(): UserFoodDatabase
}
