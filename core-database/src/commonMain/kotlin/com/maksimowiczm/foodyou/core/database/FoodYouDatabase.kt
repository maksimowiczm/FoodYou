package com.maksimowiczm.foodyou.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.core.database.diary.InitializeMealsCallback
import com.maksimowiczm.foodyou.core.database.diary.MealDatabase
import com.maksimowiczm.foodyou.core.database.diary.MealEntity
import com.maksimowiczm.foodyou.core.database.food.FoodDatabase
import com.maksimowiczm.foodyou.core.database.food.ProductEntity
import com.maksimowiczm.foodyou.core.database.food.RecipeEntity
import com.maksimowiczm.foodyou.core.database.food.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementDatabase
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementEntity
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementTypeConverter
import com.maksimowiczm.foodyou.core.database.search.SearchDatabase
import com.maksimowiczm.foodyou.core.database.search.SearchQueryEntity

@Database(
    entities = [
        ProductEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        MealEntity::class,
        MeasurementEntity::class,
        SearchQueryEntity::class
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
        AutoMigration(from = 16, to = 17),
        AutoMigration(from = 17, to = 18),
        /**
         * @see [MIGRATION_18_19]
         * Merge product and recipe measurements into MeasurementEntity
         */
        AutoMigration(from = 19, to = 20)
    ]
)
@TypeConverters(
    MeasurementTypeConverter::class
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    FoodDatabase,
    MeasurementDatabase,
    SearchDatabase,
    MealDatabase {

    companion object {
        const val VERSION = 20

        private val migrations: List<Migration> = listOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_7_8,
            MIGRATION_8_9,
            MIGRATION_11_12,
            MIGRATION_18_19
        )

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase {
            migrations.forEach(::addMigrations)
            addCallback(InitializeMealsCallback())
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
                WHERE productSource = 1
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

// Delete product source from ProductEntity
// Merge product and recipe measurements into MeasurementEntity
// Update RecipeIngredientEntity to use ingredientProductId and ingredientRecipeId column names
private val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(connection: SQLiteConnection): Unit = with(connection) {
        mergeMeasurements()
        deleteProductSource()
        updateRecipeIngredient()
    }

    private fun SQLiteConnection.mergeMeasurements() {
        // Create new MeasurementEntity table
        execSQL(
            """
            CREATE TABLE IF NOT EXISTS MeasurementEntity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                mealId INTEGER NOT NULL,
                epochDay INTEGER NOT NULL,
                productId INTEGER,
                recipeId INTEGER,
                measurement INTEGER NOT NULL,
                quantity REAL NOT NULL,
                createdAt INTEGER NOT NULL,
                isDeleted INTEGER NOT NULL,
                FOREIGN KEY (mealId) REFERENCES MealEntity(id) ON DELETE CASCADE,
                FOREIGN KEY (productId) REFERENCES ProductEntity(id) ON DELETE CASCADE,
                FOREIGN KEY (recipeId) REFERENCES RecipeEntity(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // Create proper indices for MeasurementEntity
        execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_epochDay
            ON MeasurementEntity (epochDay)
            """.trimIndent()
        )

        execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_productId
            ON MeasurementEntity (productId)
            """.trimIndent()
        )

        execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_recipeId
            ON MeasurementEntity (recipeId)
            """.trimIndent()
        )

        execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_mealId
            ON MeasurementEntity (mealId)
            """.trimIndent()
        )

        // Move product measurements to MeasurementEntity
        execSQL(
            """
            INSERT INTO MeasurementEntity (
                mealId, epochDay, productId, measurement, quantity, createdAt, isDeleted
            )
            SELECT 
                mealId, diaryEpochDay as epochDay, productId, measurement, quantity, createdAt, isDeleted
            FROM ProductMeasurementEntity
            WHERE true
            """.trimIndent()
        )

        // Move recipe measurements to MeasurementEntity
        execSQL(
            """
            INSERT INTO MeasurementEntity (
                mealId, epochDay, recipeId, measurement, quantity, createdAt, isDeleted
            )
            SELECT 
                mealId, epochDay, recipeId, measurement, quantity, createdAt, isDeleted
            FROM RecipeMeasurementEntity
            WHERE true
            """.trimIndent()
        )

        // Drop ProductMeasurementEntity and RecipeMeasurementEntity tables
        execSQL("DROP TABLE IF EXISTS ProductMeasurementEntity")
        execSQL("DROP TABLE IF EXISTS RecipeMeasurementEntity")
    }

    private fun SQLiteConnection.deleteProductSource() {
        // Create a temporary table to hold the data without productSource
        execSQL(
            "CREATE TABLE IF NOT EXISTS `ProductEntity_temp` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `brand` TEXT, `barcode` TEXT, `packageWeight` REAL, `servingWeight` REAL, `proteins` REAL NOT NULL, `carbohydrates` REAL NOT NULL, `fats` REAL NOT NULL, `calories` REAL NOT NULL, `saturatedFats` REAL, `monounsaturatedFats` REAL, `polyunsaturatedFats` REAL, `omega3` REAL, `omega6` REAL, `sugars` REAL, `salt` REAL, `fiber` REAL, `cholesterolMilli` REAL, `caffeineMilli` REAL, `vitaminAMicro` REAL, `vitaminB1Milli` REAL, `vitaminB2Milli` REAL, `vitaminB3Milli` REAL, `vitaminB5Milli` REAL, `vitaminB6Milli` REAL, `vitaminB7Micro` REAL, `vitaminB9Micro` REAL, `vitaminB12Micro` REAL, `vitaminCMilli` REAL, `vitaminDMicro` REAL, `vitaminEMilli` REAL, `vitaminKMicro` REAL, `manganeseMilli` REAL, `magnesiumMilli` REAL, `potassiumMilli` REAL, `calciumMilli` REAL, `copperMilli` REAL, `zincMilli` REAL, `sodiumMilli` REAL, `ironMilli` REAL, `phosphorusMilli` REAL, `seleniumMicro` REAL, `iodineMicro` REAL)"
        )

        // Copy data from ProductEntity to the temporary table without productSource
        execSQL(
            """
            INSERT INTO `ProductEntity_temp` (
                id, name, brand, barcode, packageWeight, servingWeight, proteins, carbohydrates, fats, calories, saturatedFats, monounsaturatedFats, polyunsaturatedFats, omega3, omega6, sugars, salt, fiber, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, iodineMicro
            )
            SELECT 
                id, name, brand, barcode, packageWeight, servingWeight, proteins, carbohydrates, fats, calories, saturatedFats, monounsaturatedFats, polyunsaturatedFats, omega3, omega6, sugars, salt, fiber, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, iodineMicro
            FROM ProductEntity
            WHERE true
            """.trimIndent()
        )

        // Drop the original ProductEntity table
        execSQL("DROP TABLE IF EXISTS ProductEntity")

        // Create a new ProductEntity table without productSource
        execSQL(
            "CREATE TABLE IF NOT EXISTS `ProductEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `brand` TEXT, `barcode` TEXT, `packageWeight` REAL, `servingWeight` REAL, `proteins` REAL NOT NULL, `carbohydrates` REAL NOT NULL, `fats` REAL NOT NULL, `calories` REAL NOT NULL, `saturatedFats` REAL, `monounsaturatedFats` REAL, `polyunsaturatedFats` REAL, `omega3` REAL, `omega6` REAL, `sugars` REAL, `salt` REAL, `fiber` REAL, `cholesterolMilli` REAL, `caffeineMilli` REAL, `vitaminAMicro` REAL, `vitaminB1Milli` REAL, `vitaminB2Milli` REAL, `vitaminB3Milli` REAL, `vitaminB5Milli` REAL, `vitaminB6Milli` REAL, `vitaminB7Micro` REAL, `vitaminB9Micro` REAL, `vitaminB12Micro` REAL, `vitaminCMilli` REAL, `vitaminDMicro` REAL, `vitaminEMilli` REAL, `vitaminKMicro` REAL, `manganeseMilli` REAL, `magnesiumMilli` REAL, `potassiumMilli` REAL, `calciumMilli` REAL, `copperMilli` REAL, `zincMilli` REAL, `sodiumMilli` REAL, `ironMilli` REAL, `phosphorusMilli` REAL, `seleniumMicro` REAL, `iodineMicro` REAL)"
        )

        // Copy data back from the temporary table to the new ProductEntity table
        execSQL(
            """
            INSERT INTO `ProductEntity` (
                id, name, brand, barcode, packageWeight, servingWeight, proteins, carbohydrates, fats, calories, saturatedFats, monounsaturatedFats, polyunsaturatedFats, omega3, omega6, sugars, salt, fiber, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, iodineMicro
            )
            SELECT 
                id, name, brand, barcode, packageWeight, servingWeight, proteins, carbohydrates, fats, calories, saturatedFats, monounsaturatedFats, polyunsaturatedFats, omega3, omega6, sugars, salt, fiber, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, iodineMicro
            FROM ProductEntity_temp
            """.trimIndent()
        )

        // Drop the temporary table
        execSQL("DROP TABLE IF EXISTS ProductEntity_temp")
    }

    private fun SQLiteConnection.updateRecipeIngredient() {
        // Create a temporary table to hold the data
        execSQL(
            """
            CREATE TABLE IF NOT EXISTS `RecipeIngredientEntity_temp` (
                `id` INTEGER,
                `recipeId` INTEGER NOT NULL,
                `ingredientProductId` INTEGER,
                `ingredientRecipeId` INTEGER,
                `measurement` INTEGER NOT NULL,
                `quantity` REAL NOT NULL
            )
            """.trimIndent()
        )

        // Copy data from RecipeIngredientEntity to the temporary table
        execSQL(
            """
            INSERT INTO `RecipeIngredientEntity_temp` (
                id, recipeId, ingredientProductId, ingredientRecipeId, measurement, quantity
            )
            SELECT 
                id, recipeId, productId AS ingredientProductId, recipeIngredientId AS ingredientRecipeId, measurement, quantity
            FROM RecipeIngredientEntity
            WHERE true
            """.trimIndent()
        )

        // Drop the original RecipeIngredientEntity table
        execSQL("DROP TABLE IF EXISTS RecipeIngredientEntity")

        // Create a new RecipeIngredientEntity table with the updated structure
        execSQL(
            """
            CREATE TABLE IF NOT EXISTS `RecipeIngredientEntity` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `recipeId` INTEGER NOT NULL,
                `ingredientProductId` INTEGER,
                `ingredientRecipeId` INTEGER,
                `measurement` INTEGER NOT NULL,
                `quantity` REAL NOT NULL,
                FOREIGN KEY (recipeId) REFERENCES RecipeEntity(id) ON DELETE CASCADE,
                FOREIGN KEY (ingredientProductId) REFERENCES ProductEntity(id) ON DELETE CASCADE,
                FOREIGN KEY (ingredientRecipeId) REFERENCES RecipeEntity(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // Create proper indices for RecipeIngredientEntity
        execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_RecipeIngredientEntity_recipeId 
            ON RecipeIngredientEntity (recipeId)
            """.trimIndent()
        )

        execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_RecipeIngredientEntity_ingredientProductId 
            ON RecipeIngredientEntity (ingredientProductId)
            """.trimIndent()
        )

        execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_RecipeIngredientEntity_ingredientRecipeId 
            ON RecipeIngredientEntity (ingredientRecipeId)
            """.trimIndent()
        )

        // Copy data back from the temporary table to the new RecipeIngredientEntity table
        execSQL(
            """
            INSERT INTO `RecipeIngredientEntity` (
                id, recipeId, ingredientProductId, ingredientRecipeId, measurement, quantity
            )
            SELECT 
                id, recipeId, ingredientProductId, ingredientRecipeId, measurement, quantity
            FROM RecipeIngredientEntity_temp
            """.trimIndent()
        )

        // Drop the temporary table
        execSQL("DROP TABLE IF EXISTS RecipeIngredientEntity_temp")
    }
}
