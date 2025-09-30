package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.immediateTransaction
import androidx.room.migration.Migration
import androidx.room.useWriterConnection
import com.maksimowiczm.foodyou.app.infrastructure.room.migration.LegacyMigrations
import com.maksimowiczm.foodyou.app.infrastructure.room.migration.deleteUsedFoodEvent
import com.maksimowiczm.foodyou.app.infrastructure.room.migration.fixMeasurementSuggestions
import com.maksimowiczm.foodyou.app.infrastructure.room.migration.foodYou3Migration
import com.maksimowiczm.foodyou.app.infrastructure.room.migration.unlinkDiaryMigration
import com.maksimowiczm.foodyou.common.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.common.domain.database.TransactionScope as DomainTransactionScope
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodSourceTypeConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementTypeConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.RoomTransactionScope
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodDatabase
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodEventEntity
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodEventTypeConverter
import com.maksimowiczm.foodyou.food.infrastructure.room.LatestMeasurementSuggestion
import com.maksimowiczm.foodyou.food.infrastructure.room.MeasurementSuggestionEntity
import com.maksimowiczm.foodyou.food.infrastructure.room.ProductEntity
import com.maksimowiczm.foodyou.food.infrastructure.room.ProductFts
import com.maksimowiczm.foodyou.food.infrastructure.room.RecipeEntity
import com.maksimowiczm.foodyou.food.infrastructure.room.RecipeFts
import com.maksimowiczm.foodyou.food.infrastructure.room.RecipeIngredientEntity
import com.maksimowiczm.foodyou.food.search.infrastructure.room.FoodSearchDatabase
import com.maksimowiczm.foodyou.food.search.infrastructure.room.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.food.search.infrastructure.room.RecipeAllIngredientsView
import com.maksimowiczm.foodyou.food.search.infrastructure.room.SearchEntry
import com.maksimowiczm.foodyou.food.search.infrastructure.room.USDAPagingKeyEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.DiaryProductEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.DiaryRecipeEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.InitializeMealsCallback
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.ManualDiaryEntryEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.MealEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.MeasurementEntity
import com.maksimowiczm.foodyou.sponsorship.infrastructure.room.SponsorshipDatabase
import com.maksimowiczm.foodyou.sponsorship.infrastructure.room.SponsorshipEntity

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
            ProductFts::class,
            RecipeFts::class,
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
            AutoMigration(from = 29, to = 30), // Add MeasurementSuggestion indices
            AutoMigration(from = 30, to = 31), // Add ProductFts and RecipeFts
        ],
)
@TypeConverters(
    FoodSourceTypeConverter::class,
    MeasurementTypeConverter::class,
    FoodEventTypeConverter::class,
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    TransactionProvider,
    FoodDatabase,
    FoodSearchDatabase,
    FoodDiaryDatabase,
    SponsorshipDatabase {

    override suspend fun <T> withTransaction(block: suspend DomainTransactionScope<T>.() -> T): T =
        useWriterConnection {
            it.immediateTransaction {
                val scope = RoomTransactionScope<T>(this)
                scope.block()
            }
        }

    companion object {
        const val VERSION = 31

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
