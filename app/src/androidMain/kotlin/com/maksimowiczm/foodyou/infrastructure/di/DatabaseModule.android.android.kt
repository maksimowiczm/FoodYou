package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.database.callback.AndroidInitializeMealsCallback
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase.Companion.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val databaseModule = module {
    single {
        val builder: RoomDatabase.Builder<OpenSourceDatabase> =
            Room.databaseBuilder(
                context = androidContext(),
                klass = OpenSourceDatabase::class.java,
                name = "open_source_database.db"
            )

        builder.buildDatabase(AndroidInitializeMealsCallback(androidContext()))
    }
    factory { database().productDao() }
    factory { database().addFoodDao() }
    factory { database().openFoodFactsDao() }
}
