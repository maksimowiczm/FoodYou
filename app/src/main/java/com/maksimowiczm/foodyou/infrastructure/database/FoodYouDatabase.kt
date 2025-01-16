package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.MealProductEntity
import com.maksimowiczm.foodyou.feature.diary.database.ProductEntity
import com.maksimowiczm.foodyou.feature.diary.database.WeightUnitConverter

@Database(
    entities = [
        // Diary
        MealProductEntity::class,
        ProductEntity::class
    ],
    version = FoodYouDatabase.VERSION
)
@TypeConverters(
    // Diary
    WeightUnitConverter::class
)
abstract class FoodYouDatabase : DiaryDatabase, RoomDatabase() {

    companion object {
        const val VERSION = 1

        val migrations: List<Migration> = emptyList()

        fun Builder<FoodYouDatabase>.buildDatabase(): DiaryDatabase {
            migrations.forEach(::addMigrations)
            return build()
        }
    }
}
