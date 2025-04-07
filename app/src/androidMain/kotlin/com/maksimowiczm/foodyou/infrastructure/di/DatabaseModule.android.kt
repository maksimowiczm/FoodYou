package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.feature.diary.core.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.core.database.meal.InitializeMealsCallback
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase.Companion.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.binds
import org.koin.dsl.module

actual val databaseModule = module {
    single {
        val builder: RoomDatabase.Builder<OpenSourceDatabase> =
            Room.databaseBuilder(
                context = androidContext(),
                klass = OpenSourceDatabase::class.java,
                name = DATABASE_NAME
            )

        builder.buildDatabase(InitializeMealsCallback(androidContext()))
    }.binds(
        arrayOf(
            DiaryDatabase::class
        )
    )
}
