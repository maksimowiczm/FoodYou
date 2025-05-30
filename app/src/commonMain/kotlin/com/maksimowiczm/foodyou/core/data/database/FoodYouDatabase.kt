package com.maksimowiczm.foodyou.core.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.core.data.database.food.FoodDao
import com.maksimowiczm.foodyou.core.data.database.meal.InitializeMealsCallback
import com.maksimowiczm.foodyou.core.data.database.meal.MealDao
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementTypeConverter
import com.maksimowiczm.foodyou.core.data.database.measurement.ProductMeasurementDao
import com.maksimowiczm.foodyou.core.data.database.measurement.RecipeMeasurementDao
import com.maksimowiczm.foodyou.core.data.database.product.ProductDao
import com.maksimowiczm.foodyou.core.data.database.product.ProductSourceConverter
import com.maksimowiczm.foodyou.core.data.database.product.ProductSourceSQLConstants.OPEN_FOOD_FACTS
import com.maksimowiczm.foodyou.core.data.database.recipe.RecipeDao
import com.maksimowiczm.foodyou.core.data.database.search.SearchDao
import com.maksimowiczm.foodyou.core.data.model.meal.MealEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientProductDetails
import com.maksimowiczm.foodyou.core.data.model.search.SearchQueryEntity

@Database(
    entities = [
        MealEntity::class,
        ProductEntity::class,
        ProductMeasurementEntity::class,
        SearchQueryEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        RecipeMeasurementEntity::class
    ],
    views = [
        RecipeIngredientProductDetails::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [
        /**
         * @see [MIGRATION_1_2]
         * Add rank to MealEntity
         */
        /**
         * @see [MIGRATION_2_3]
         * 2.0.0 schema change
         */
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        /**
         * @see [MIGRATION_7_8]
         * Remove unused products from OpenFoodFacts source
         */
        /**
         * @see [MIGRATION_8_9]
         * Remove OpenFoodFactsPagingKeyEntity
         */
        AutoMigration(from = 9, to = 10, spec = MIGRATION_9_10::class),
        AutoMigration(from = 10, to = 11),
        /**
         * @see [MIGRATION_11_12]
         * Fix sodium value in ProductEntity. Convert grams to milligrams.
         */
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 14, to = 15),
        AutoMigration(from = 15, to = 16),
        AutoMigration(from = 16, to = 17)
    ]
)
@TypeConverters(
    ProductSourceConverter::class,
    MeasurementTypeConverter::class
)
abstract class FoodYouDatabase : RoomDatabase() {
    abstract val mealDao: MealDao
    abstract val recipeMeasurementDao: RecipeMeasurementDao
    abstract val productMeasurementDao: ProductMeasurementDao
    abstract val productDao: ProductDao
    abstract val searchDao: SearchDao
    abstract val recipeDao: RecipeDao
    abstract val foodDao: FoodDao

    companion object {
        const val VERSION = 17

        private val migrations: List<Migration> = listOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_11_12
        )

        fun Builder<FoodYouDatabase>.buildDatabase(
            initializeMealsCallback: InitializeMealsCallback
        ): FoodYouDatabase {
            migrations.forEach(::addMigrations)
            addCallback(initializeMealsCallback)
            return build()
        }
    }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE MealEntity 
            ADD COLUMN rank INTEGER NOT NULL DEFAULT -1
            """.trimIndent()
        )
        connection.execSQL(
            """
            UPDATE MealEntity 
            SET rank = id
            """.trimIndent()
        )
    }
}

// API < 30 lack support for ALTER TABLE commands so there is a lot of temp tables
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SQLiteConnection) {
        // Change OpenFoodFactsPagingKey to OpenFoodFactsPagingKeyEntity
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS OpenFoodFactsPagingKeyEntity(
                queryString TEXT NOT NULL,
                country TEXT NOT NULL,
                fetchedCount INTEGER NOT NULL,
                totalCount INTEGER NOT NULL,
                PRIMARY KEY(queryString, country)
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            INSERT INTO OpenFoodFactsPagingKeyEntity (queryString, country, fetchedCount, totalCount)
            SELECT queryString, country, fetchedCount, totalCount FROM OpenFoodFactsPagingKey
            """.trimIndent()
        )
        database.execSQL("DROP TABLE OpenFoodFactsPagingKey")

        // Create new ProductEntity structure
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ProductEntity_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                brand TEXT,
                barcode TEXT,
                packageWeight REAL,
                servingWeight REAL,
                productSource INTEGER NOT NULL,
                calories REAL NOT NULL,
                proteins REAL NOT NULL,
                carbohydrates REAL NOT NULL,
                sugars REAL,
                fats REAL NOT NULL,
                saturatedFats REAL,
                salt REAL,
                sodium REAL,
                fiber REAL
            )
            """.trimIndent()
        )

        // Move data to temp table
        database.execSQL(
            """
            INSERT INTO ProductEntity_temp (
                id, name, brand, barcode,
                packageWeight, servingWeight, productSource,
                calories, proteins, carbohydrates, sugars, fats, saturatedFats,
                salt, sodium, fiber
            )
            SELECT 
                id, name, brand, barcode,
                packageWeight, servingWeight, productSource,
                calories, proteins, carbohydrates, sugars, fats, saturatedFats,
                salt, sodium, fiber
            FROM ProductEntity
            """.trimIndent()
        )

        database.execSQL("DROP TABLE ProductEntity")
        database.execSQL("ALTER TABLE ProductEntity_temp RENAME TO ProductEntity")

        // Create ProductMeasurementEntity
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ProductMeasurementEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mealId INTEGER NOT NULL,
                diaryEpochDay INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                measurement INTEGER NOT NULL,
                quantity REAL NOT NULL,
                createdAt INTEGER NOT NULL,
                isDeleted INTEGER NOT NULL,
                FOREIGN KEY (productId) REFERENCES ProductEntity(id) ON DELETE CASCADE,
                FOREIGN KEY (mealId) REFERENCES MealEntity(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // Create proper indices for ProductMeasurementEntity
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_productId 
            ON ProductMeasurementEntity (productId)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_isDeleted 
            ON ProductMeasurementEntity (isDeleted)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_mealId 
            ON ProductMeasurementEntity (mealId)
            """.trimIndent()
        )

        // Migrate data from WeightMeasurementEntity to ProductMeasurementEntity
        database.execSQL(
            """
            INSERT INTO ProductMeasurementEntity (
                id, mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
            )
            SELECT 
                id, mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
            FROM WeightMeasurementEntity
            """.trimIndent()
        )

        database.execSQL("DROP TABLE WeightMeasurementEntity")

        // Create SearchQueryEntity from ProductQueryEntity
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS SearchQueryEntity (
                query TEXT NOT NULL PRIMARY KEY,
                epochSeconds INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Migrate data from ProductQueryEntity to SearchQueryEntity
        database.execSQL(
            """
            INSERT INTO SearchQueryEntity (query, epochSeconds)
            SELECT query, date FROM ProductQueryEntity
            """.trimIndent()
        )

        database.execSQL("DROP TABLE ProductQueryEntity")
    }
}

// Delete unused products from OpenFoodFacts source
private val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            WITH UsedInRecipes AS (
                SELECT DISTINCT productId 
                FROM RecipeIngredientEntity i
            ),
            UsedInMeals AS (
                SELECT DISTINCT productId 
                FROM ProductMeasurementEntity m
            ),
            UsedProducts AS (
                SELECT DISTINCT productId 
                FROM UsedInRecipes
                UNION
                SELECT DISTINCT productId 
                FROM UsedInMeals
            )
            DELETE FROM ProductEntity 
            WHERE id IN (
                SELECT id 
                FROM ProductEntity 
                WHERE productSource = $OPEN_FOOD_FACTS
                AND id NOT IN (SELECT productId FROM UsedProducts)
            ) 
            """.trimIndent()
        )
    }
}

private val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            DROP TABLE IF EXISTS OpenFoodFactsPagingKeyEntity
            """.trimIndent()
        )
    }
}

@Suppress("ClassName")
@RenameColumn(
    tableName = "ProductEntity",
    fromColumnName = "sodium",
    toColumnName = "sodiumMilli"
)
class MIGRATION_9_10 : AutoMigrationSpec

private val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            UPDATE ProductEntity 
            SET sodiumMilli = sodiumMilli * 1000
            WHERE sodiumMilli IS NOT NULL
            """.trimIndent()
        )
    }
}
