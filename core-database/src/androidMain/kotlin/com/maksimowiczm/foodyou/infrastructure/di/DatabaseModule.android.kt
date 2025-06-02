package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.core.database.diary.DiaryDatabase
import com.maksimowiczm.foodyou.core.database.food.FoodDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
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

        builder.buildDatabase()
    }.binds(
        arrayOf(
            FoodDatabase::class,
            DiaryDatabase::class
        )
    )

    factory { database.foodLocalDataSource }
}

private val Scope.database
    get() = get<FoodYouDatabase>()
