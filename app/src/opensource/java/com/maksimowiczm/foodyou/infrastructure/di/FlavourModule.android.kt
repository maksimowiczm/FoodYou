package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.feature.addfood.database.AndroidInitializeMealsCallback
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.ProductDatabase
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase
import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase.Companion.buildDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.binds
import org.koin.dsl.module

val flavourModule = module {
    single {
        val builder: RoomDatabase.Builder<OpenSourceDatabase> =
            Room.databaseBuilder(
                context = androidContext(),
                klass = OpenSourceDatabase::class.java,
                name = "open_source_database.db"
            )

        builder.buildDatabase(AndroidInitializeMealsCallback(androidContext()))
    }.binds(
        classes = arrayOf(
            ProductDatabase::class,
            AddFoodDatabase::class,
            OpenFoodFactsDatabase::class
        )
    )
}
