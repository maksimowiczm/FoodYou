package com.maksimowiczm.foodyou.infrastructure.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.core.data.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.data.database.FoodYouDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.core.data.database.meal.InitializeMealsCallback
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.MealLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.ProductLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.ProductMeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.RecipeMeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.SearchLocalDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.bind
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
    }

    factory { database.foodDao }.bind<FoodLocalDataSource>()
    factory { database.mealDao }.bind<MealLocalDataSource>()
    factory { database.productDao }.bind<ProductLocalDataSource>()
    factory { database.productMeasurementDao }.bind<ProductMeasurementLocalDataSource>()
    factory { database.recipeDao }.bind<RecipeLocalDataSource>()
    factory { database.recipeMeasurementDao }.bind<RecipeMeasurementLocalDataSource>()
    factory { database.searchDao }.bind<SearchLocalDataSource>()
}

private val Scope.database
    get() = get<FoodYouDatabase>()
