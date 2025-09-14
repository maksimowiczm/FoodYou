package com.maksimowiczm.foodyou.app.infrastructure.opensource.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.immediateTransaction
import androidx.room.migration.Migration
import androidx.room.useWriterConnection
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.FoodEventDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.FoodEventEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.FoodEventTypeConverter
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.LatestMeasurementSuggestion
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.MeasurementSuggestionDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.MeasurementSuggestionEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.ProductDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.ProductEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.RecipeDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.RecipeEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room.RecipeIngredientEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.DiaryProductEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.DiaryRecipeEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.InitializeMealsCallback
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.ManualDiaryEntryDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.ManualDiaryEntryEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.MealDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.MealEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.MeasurementDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.MeasurementEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.room.FoodSearchDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.room.RecipeAllIngredientsView
import com.maksimowiczm.foodyou.app.infrastructure.opensource.foodsearch.room.SearchEntry
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.room.OpenFoodFactsDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.room.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.migration.LegacyMigrations
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.migration.deleteUsedFoodEvent
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.migration.fixMeasurementSuggestions
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.migration.foodYou3Migration
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.migration.unlinkDiaryMigration
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.FoodSourceTypeConverter
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.MeasurementTypeConverter
import com.maksimowiczm.foodyou.app.infrastructure.opensource.shared.room.RoomTransactionScope
import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.room.SponsorshipDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.sponsorship.room.SponsorshipEntity
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.room.USDAPagingKeyDao
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.room.USDAPagingKeyEntity
import com.maksimowiczm.foodyou.shared.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.domain.database.TransactionScope as DomainTransactionScope

@Database(
    entities =
        [
            ProductEntity::class,
            RecipeEntity::class,
            RecipeIngredientEntity::class,
            OpenFoodFactsPagingKeyEntity::class,
            USDAPagingKeyEntity::class,
            FoodEventEntity::class,
            SearchEntry::class,
            MealEntity::class,
            MeasurementEntity::class,
            DiaryProductEntity::class,
            DiaryRecipeEntity::class,
            DiaryRecipeIngredientEntity::class,
            SponsorshipEntity::class,
            MeasurementSuggestionEntity::class,
            ManualDiaryEntryEntity::class,
        ],
    views = [RecipeAllIngredientsView::class, LatestMeasurementSuggestion::class],
    version = FoodYouDatabase.VERSION,
    exportSchema = true,
    autoMigrations =
        [
            /** @see [LegacyMigrations.MIGRATION_1_2] Add rank to MealEntity */
            /** @see [LegacyMigrations.MIGRATION_2_3] 2.0.0 schema change */
            AutoMigration(from = 3, to = 4),
            AutoMigration(from = 4, to = 5),
            AutoMigration(from = 5, to = 6),
            AutoMigration(from = 6, to = 7),
            /**
             * @see [LegacyMigrations.MIGRATION_7_8] Remove unused products from OpenFoodFacts
             *   source
             */
            /** @see [LegacyMigrations.MIGRATION_8_9] Remove OpenFoodFactsPagingKeyEntity */
            AutoMigration(from = 9, to = 10, spec = LegacyMigrations.MIGRATION_9_10::class),
            AutoMigration(from = 10, to = 11),
            /**
             * @see [LegacyMigrations.MIGRATION_11_12] Fix sodium value in ProductEntity. Convert
             *   grams to milligrams.
             */
            AutoMigration(from = 12, to = 13),
            AutoMigration(from = 13, to = 14),
            AutoMigration(from = 14, to = 15),
            AutoMigration(from = 15, to = 16),
            AutoMigration(from = 16, to = 17),
            AutoMigration(from = 17, to = 18),
            /**
             * @see [LegacyMigrations.MIGRATION_18_19] Merge product and recipe measurements into
             *   MeasurementEntity
             */
            AutoMigration(from = 19, to = 20),
            /**
             * @see [LegacyMigrations.MIGRATION_20_21] Add isLiquid column to ProductEntity and
             *   RecipeEntity
             */
            /**
             * @see [LegacyMigrations.MIGRATION_21_22] Add `note` column to ProductEntity and
             *   RecipeEntity
             */
            AutoMigration(from = 23, to = 24), // Add LatestFoodMeasuredEventView
            AutoMigration(from = 24, to = 25), // Add FoodEventEntity onDelete cascade
            AutoMigration(from = 28, to = 29), // Add ManualDiaryEntryEntity
        ],
)
@TypeConverters(
    FoodSourceTypeConverter::class,
    MeasurementTypeConverter::class,
    FoodEventTypeConverter::class,
)
abstract class FoodYouDatabase : RoomDatabase(), TransactionProvider {
    abstract val productDao: ProductDao
    abstract val recipeDao: RecipeDao
    abstract val foodSearchDao: FoodSearchDao
    abstract val openFoodFactsDao: OpenFoodFactsDao
    abstract val usdaPagingKeyDao: USDAPagingKeyDao
    abstract val foodEventDao: FoodEventDao
    abstract val measurementDao: MeasurementDao
    abstract val mealDao: MealDao
    abstract val sponsorshipDao: SponsorshipDao
    abstract val measurementSuggestionDao: MeasurementSuggestionDao
    abstract val manualDiaryEntryDao: ManualDiaryEntryDao

    override suspend fun <T> withTransaction(block: suspend DomainTransactionScope<T>.() -> T): T =
        useWriterConnection {
            it.immediateTransaction {
                val scope = RoomTransactionScope<T>(this)
                scope.block()
            }
        }

    companion object {
        const val VERSION = 29

        private val migrations: List<Migration> =
            listOf(
                LegacyMigrations.MIGRATION_1_2,
                LegacyMigrations.MIGRATION_2_3,
                LegacyMigrations.MIGRATION_7_8,
                LegacyMigrations.MIGRATION_8_9,
                LegacyMigrations.MIGRATION_11_12,
                LegacyMigrations.MIGRATION_18_19,
                LegacyMigrations.MIGRATION_20_21,
                LegacyMigrations.MIGRATION_21_22,
                foodYou3Migration,
                unlinkDiaryMigration,
                deleteUsedFoodEvent,
                fixMeasurementSuggestions,
            )

        fun Builder<FoodYouDatabase>.buildDatabase(
            mealsCallback: InitializeMealsCallback
        ): FoodYouDatabase {
            addMigrations(*migrations.toTypedArray())
            addCallback(mealsCallback)
            return build()
        }
    }
}
