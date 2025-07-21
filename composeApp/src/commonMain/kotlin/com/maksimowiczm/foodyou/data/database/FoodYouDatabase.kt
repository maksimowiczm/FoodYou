package com.maksimowiczm.foodyou.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.feature.about.data.database.AboutDatabase
import com.maksimowiczm.foodyou.feature.about.data.database.SponsorMethodConverter
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.SourceTypeConverter
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product
import com.maksimowiczm.foodyou.feature.food.data.database.food.Recipe
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeIngredient
import com.maksimowiczm.foodyou.feature.food.data.database.openfoodfacts.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.feature.food.data.database.search.RecipeAllIngredientsView
import com.maksimowiczm.foodyou.feature.food.data.database.search.SearchEntry
import com.maksimowiczm.foodyou.feature.food.data.database.usda.USDAPagingKey
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.fooddiary.data.Meal
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement
import com.maksimowiczm.foodyou.feature.measurement.data.MeasurementTypeConverter

@Database(
    entities = [
        Sponsorship::class,
        Product::class,
        Recipe::class,
        RecipeIngredient::class,
        Meal::class,
        Measurement::class,
        OpenFoodFactsPagingKey::class,
        SearchEntry::class,
        USDAPagingKey::class
    ],
    views = [
        RecipeAllIngredientsView::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(
    SponsorMethodConverter::class,
    MeasurementTypeConverter::class,
    SourceTypeConverter::class
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    AboutDatabase,
    FoodDatabase,
    FoodDiaryDatabase {

    companion object {
        const val VERSION = 1

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase = this
            .addCallback(InitializeMealsCallback())
            .build()
    }
}
