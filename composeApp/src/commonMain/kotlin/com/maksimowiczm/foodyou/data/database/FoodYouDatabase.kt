package com.maksimowiczm.foodyou.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.maksimowiczm.foodyou.feature.about.data.database.AboutDatabase
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.SourceTypeConverter
import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodEvent
import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodEventTypeConverter
import com.maksimowiczm.foodyou.feature.food.data.database.food.LatestFoodMeasuredEventView
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
        USDAPagingKey::class,
        FoodEvent::class
    ],
    views = [
        RecipeAllIngredientsView::class,
        LatestFoodMeasuredEventView::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [
        /**
         * @see [LegacyMigrations.MIGRATION_1_2]
         * Add rank to MealEntity
         */
        /**
         * @see [LegacyMigrations.MIGRATION_2_3]
         * 2.0.0 schema change
         */
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        /**
         * @see [LegacyMigrations.MIGRATION_7_8]
         * Remove unused products from OpenFoodFacts source
         */
        /**
         * @see [LegacyMigrations.MIGRATION_8_9]
         * Remove OpenFoodFactsPagingKeyEntity
         */
        AutoMigration(from = 9, to = 10, spec = LegacyMigrations.MIGRATION_9_10::class),
        AutoMigration(from = 10, to = 11),
        /**
         * @see [LegacyMigrations.MIGRATION_11_12]
         * Fix sodium value in ProductEntity. Convert grams to milligrams.
         */
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 14, to = 15),
        AutoMigration(from = 15, to = 16),
        AutoMigration(from = 16, to = 17),
        AutoMigration(from = 17, to = 18),
        /**
         * @see [LegacyMigrations.MIGRATION_18_19]
         * Merge product and recipe measurements into MeasurementEntity
         */
        AutoMigration(from = 19, to = 20),
        /**
         * @see [LegacyMigrations.MIGRATION_20_21]
         * Add isLiquid column to ProductEntity and RecipeEntity
         */
        /**
         * @see [LegacyMigrations.MIGRATION_21_22]
         * Add `note` column to ProductEntity and RecipeEntity
         */
        AutoMigration(from = 23, to = 24), // Add LatestFoodMeasuredEventView
        AutoMigration(from = 24, to = 25) // Add FoodEventEntity onDelete cascade
    ]
)
@TypeConverters(
    MeasurementTypeConverter::class,
    SourceTypeConverter::class,
    FoodEventTypeConverter::class
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    AboutDatabase,
    FoodDatabase,
    FoodDiaryDatabase {

    companion object {
        const val VERSION = 25

        private val migrations: List<Migration> = listOf(
            LegacyMigrations.MIGRATION_1_2,
            LegacyMigrations.MIGRATION_2_3,
            LegacyMigrations.MIGRATION_7_8,
            LegacyMigrations.MIGRATION_8_9,
            LegacyMigrations.MIGRATION_11_12,
            LegacyMigrations.MIGRATION_18_19,
            LegacyMigrations.MIGRATION_20_21,
            LegacyMigrations.MIGRATION_21_22,
            foodYou3Migration
        )

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase {
            addMigrations(*migrations.toTypedArray())
            addCallback(InitializeMealsCallback())
            return build()
        }
    }
}
