package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.infrastructure.database.FoodYouDatabase
import com.maksimowiczm.foodyou.infrastructure.database.FoodYouDatabase.Companion.buildDatabase
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        val builder: RoomDatabase.Builder<FoodYouDatabase> = Room.databaseBuilder(
            context = get(),
            klass = FoodYouDatabase::class.java,
            name = "food_you_database.db"
        )

        builder.buildDatabase()
    }.bind<DiaryDatabase>()

    factory {
        get<DiaryDatabase>().diaryDao()
    }
}
