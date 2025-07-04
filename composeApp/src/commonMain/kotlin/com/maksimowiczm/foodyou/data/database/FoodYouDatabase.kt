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
import com.maksimowiczm.foodyou.feature.measurement.data.MeasurementTypeConverter

@Database(
    entities = [
        Sponsorship::class,
        Product::class,
        Recipe::class,
        RecipeIngredient::class
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
    FoodDatabase {

    companion object {
        const val VERSION = 1

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase = build()
    }
}
