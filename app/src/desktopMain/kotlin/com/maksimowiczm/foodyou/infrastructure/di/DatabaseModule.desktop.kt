package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.database.callback.InitializeMealsCallback
import com.maksimowiczm.foodyou.database.entity.MealEntity
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase.Companion.buildDatabase
import org.koin.dsl.module

actual val databaseModule = module {
    single {
        val builder: RoomDatabase.Builder<OpenSourceDatabase> =
            Room.databaseBuilder(
                name = "open_source_database.db"
            )

        val callback = object : InitializeMealsCallback() {
            override fun getMeals(): List<MealEntity> = listOf(
                MealEntity(
                    id = 1,
                    name = "Breakfast",
                    fromHour = 6,
                    fromMinute = 0,
                    toHour = 10,
                    toMinute = 0
                )
            )
        }

        builder.buildDatabase(callback)
    }
    factory { database().productDao() }
    factory { database().addFoodDao() }
    factory { database().openFoodFactsDao() }
}
