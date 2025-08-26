package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.execSQL
import androidx.room.immediateTransaction
import androidx.room.migration.Migration
import androidx.room.useWriterConnection
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseDumpService
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.TransactionScope as DomainTransactionScope
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventTypeConverter
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodSearchDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.LatestMeasurementSuggestion
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.MeasurementSuggestionDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.MeasurementSuggestionEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeAllIngredientsView
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.SearchEntry
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.InitializeMealsCallback
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MealDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MealEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration.LegacyMigrations
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration.deleteUsedFoodEvent
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration.fixMeasurementSuggestions
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration.foodYou3Migration
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration.unlinkDiaryMigration
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.openfoodfacts.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.FoodSourceTypeConverter
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.MeasurementTypeConverter
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.sponsorship.SponsorshipDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.sponsorship.SponsorshipEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.usda.USDAPagingKeyDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.usda.USDAPagingKeyEntity
import kotlinx.coroutines.flow.Flow

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
        ],
)
@TypeConverters(
    FoodSourceTypeConverter::class,
    MeasurementTypeConverter::class,
    FoodEventTypeConverter::class,
)
abstract class FoodYouDatabase : RoomDatabase(), DatabaseTransactionProvider, DatabaseDumpService {
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

    override suspend fun <T> withTransaction(block: suspend DomainTransactionScope<T>.() -> T): T =
        useWriterConnection {
            it.immediateTransaction {
                val scope = RoomTransactionScope<T>(this)
                scope.block()
            }
        }

    private lateinit var databaseFileReader: FoodYouDatabase.() -> Flow<ByteArray>

    override suspend fun provideDatabaseDump(): Flow<ByteArray> {
        // Create a checkpoint to ensure the database is in a consistent state
        useWriterConnection { connection -> connection.execSQL("PRAGMA wal_checkpoint(FULL);") }
        return databaseFileReader()
    }

    companion object {
        const val VERSION = 28

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
            mealsCallback: InitializeMealsCallback,
            databaseReader: FoodYouDatabase.() -> Flow<ByteArray>,
        ): FoodYouDatabase {
            addMigrations(*migrations.toTypedArray())
            addCallback(mealsCallback)
            return build().apply { databaseFileReader = databaseReader }
        }
    }
}
