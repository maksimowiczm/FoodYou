package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.callback.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.search.database.SearchDatabase
import com.maksimowiczm.foodyou.infrastructure.database.FoodYouDatabase
import com.maksimowiczm.foodyou.infrastructure.database.FoodYouDatabase.Companion.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.binds
import org.koin.dsl.module

actual val databaseModule = module {
    single {
        val builder: RoomDatabase.Builder<FoodYouDatabase> =
            Room.databaseBuilder(
                context = androidContext(),
                klass = FoodYouDatabase::class.java,
                name = DATABASE_NAME
            )

        builder.buildDatabase(InitializeMealsCallback(androidContext()))
    }.binds(
        arrayOf(
            SearchDatabase::class,
            DiaryDatabase::class
        )
    )
}
