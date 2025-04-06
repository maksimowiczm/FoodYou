package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.meal.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.diary.database.meal.MealEntity

@Database(
    entities = [
        MealEntity::class
    ],
//    version = OpenSourceDatabase.VERSION,
    version = 3,
    exportSchema = true
)
abstract class OpenSourceDatabase :
    RoomDatabase(),
    DiaryDatabase {
    companion object {
//        const val VERSION = 2
//
//        private val migrations: List<Migration> = listOf(
//            MIGRATION_1_2
//        )

        fun Builder<OpenSourceDatabase>.buildDatabase(
            initializeMealsCallback: InitializeMealsCallback
        ): OpenSourceDatabase {
//            migrations.forEach(::addMigrations)
            addCallback(initializeMealsCallback)
            return build()
        }
    }
}
