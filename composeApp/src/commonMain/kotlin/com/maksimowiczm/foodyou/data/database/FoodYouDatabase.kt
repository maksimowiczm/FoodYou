package com.maksimowiczm.foodyou.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.feature.about.data.database.AboutDatabase
import com.maksimowiczm.foodyou.feature.about.data.database.SponsorMethodConverter
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.food.data.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.Product
import com.maksimowiczm.foodyou.feature.food.data.Recipe
import com.maksimowiczm.foodyou.feature.food.data.RecipeIngredient
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.fooddiary.data.Meal
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.measurement.data.MeasurementTypeConverter

@Database(
    entities = [
        Sponsorship::class,
        Product::class,
        Recipe::class,
        RecipeIngredient::class,
        Meal::class,
        Measurement::class,
        OpenFoodFactsProduct::class,
        OpenFoodFactsPagingKey::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(
    SponsorMethodConverter::class,
    MeasurementTypeConverter::class
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    AboutDatabase,
    FoodDatabase,
    FoodDiaryDatabase,
    OpenFoodFactsDatabase {

    companion object {
        const val VERSION = 1

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase = this
            .addCallback(InitializeMealsCallback())
            .build()
    }
}
