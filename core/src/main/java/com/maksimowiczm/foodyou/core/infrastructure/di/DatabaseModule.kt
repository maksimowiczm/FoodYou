package com.maksimowiczm.foodyou.core.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.core.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.core.infrastructure.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.infrastructure.database.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.binds
import org.koin.dsl.module

val databaseModule = module {
    single {
        val builder: RoomDatabase.Builder<FoodYouDatabase> = Room.databaseBuilder(
            context = get(),
            klass = FoodYouDatabase::class.java,
            name = "food_you_database.db"
        )

        builder.buildDatabase(androidContext())
    }.binds(
        classes = arrayOf(
            ProductDatabase::class,
            AddFoodDatabase::class
        )
    )
}
