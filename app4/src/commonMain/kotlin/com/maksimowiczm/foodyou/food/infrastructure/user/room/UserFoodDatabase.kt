package com.maksimowiczm.foodyou.food.infrastructure.user.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [UserFoodEntity::class, UserFoodFts::class],
    version = UserFoodDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(QuantityEntityConverter::class)
abstract class UserFoodDatabase : RoomDatabase() {
    abstract val dao: UserFoodDao

    companion object {
        const val VERSION = 1

        fun Builder<UserFoodDatabase>.buildDatabase(): UserFoodDatabase = build()
    }
}
