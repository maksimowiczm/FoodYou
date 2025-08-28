package com.maksimowiczm.foodyou.business.shared.infrastructure.room.migration

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/** Migrations for FoodYou 1 and 2 versions */
object LegacyMigrations {
    val MIGRATION_1_2 =
        object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
            ALTER TABLE MealEntity 
            ADD COLUMN rank INTEGER NOT NULL DEFAULT -1
                """
                        .trimIndent()
                )
                connection.execSQL(
                    """
            UPDATE MealEntity 
            SET rank = id
                """
                        .trimIndent()
                )
            }
        }

    // API < 30 lack support for ALTER TABLE commands so there is a lot of temp tables
    val MIGRATION_2_3 =
        object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                // Change OpenFoodFactsPagingKey to OpenFoodFactsPagingKeyEntity
                connection.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS OpenFoodFactsPagingKeyEntity(
                queryString TEXT NOT NULL,
                country TEXT NOT NULL,
                fetchedCount INTEGER NOT NULL,
                totalCount INTEGER NOT NULL,
                PRIMARY KEY(queryString, country)
            )
                """
                        .trimIndent()
                )
                connection.execSQL(
                    """
            INSERT INTO OpenFoodFactsPagingKeyEntity (queryString, country, fetchedCount, totalCount)
            SELECT queryString, country, fetchedCount, totalCount FROM OpenFoodFactsPagingKey
                """
                        .trimIndent()
                )
                connection.execSQL("DROP TABLE OpenFoodFactsPagingKey")

                // Create new ProductEntity structure
                connection.execSQL(
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
                """
                        .trimIndent()
                )

                // Move data to temp table
                connection.execSQL(
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
                """
                        .trimIndent()
                )

                connection.execSQL("DROP TABLE ProductEntity")
                connection.execSQL("ALTER TABLE ProductEntity_temp RENAME TO ProductEntity")

                // Create ProductMeasurementEntity
                connection.execSQL(
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
                """
                        .trimIndent()
                )

                // Create proper indices for ProductMeasurementEntity
                connection.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_productId 
            ON ProductMeasurementEntity (productId)
                """
                        .trimIndent()
                )

                connection.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_isDeleted 
            ON ProductMeasurementEntity (isDeleted)
                """
                        .trimIndent()
                )

                connection.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_ProductMeasurementEntity_mealId 
            ON ProductMeasurementEntity (mealId)
                """
                        .trimIndent()
                )

                // Migrate data from WeightMeasurementEntity to ProductMeasurementEntity
                connection.execSQL(
                    """
            INSERT INTO ProductMeasurementEntity (
                id, mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
            )
            SELECT 
                id, mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
            FROM WeightMeasurementEntity
                """
                        .trimIndent()
                )

                connection.execSQL("DROP TABLE WeightMeasurementEntity")

                // Create SearchQueryEntity from ProductQueryEntity
                connection.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS SearchQueryEntity (
                query TEXT NOT NULL PRIMARY KEY,
                epochSeconds INTEGER NOT NULL
            )
                """
                        .trimIndent()
                )

                // Migrate data from ProductQueryEntity to SearchQueryEntity
                connection.execSQL(
                    """
            INSERT INTO SearchQueryEntity (query, epochSeconds)
            SELECT query, date FROM ProductQueryEntity
                """
                        .trimIndent()
                )

                connection.execSQL("DROP TABLE ProductQueryEntity")
            }
        }

    // Delete unused products from OpenFoodFacts source
    val MIGRATION_7_8 =
        object : Migration(7, 8) {
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
                """
                        .trimIndent()
                )
            }
        }

    val MIGRATION_8_9 =
        object : Migration(8, 9) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
            DROP TABLE IF EXISTS OpenFoodFactsPagingKeyEntity
                """
                        .trimIndent()
                )
            }
        }

    @Suppress("ClassName")
    @RenameColumn(
        tableName = "ProductEntity",
        fromColumnName = "sodium",
        toColumnName = "sodiumMilli",
    )
    class MIGRATION_9_10 : AutoMigrationSpec

    val MIGRATION_11_12 =
        object : Migration(11, 12) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
            UPDATE ProductEntity 
            SET sodiumMilli = sodiumMilli * 1000
            WHERE sodiumMilli IS NOT NULL
                """
                        .trimIndent()
                )
            }
        }

    // Delete product source from ProductEntity
    // Merge product and recipe measurements into MeasurementEntity
    // Update RecipeIngredientEntity to use ingredientProductId and ingredientRecipeId column names
    val MIGRATION_18_19 =
        object : Migration(18, 19) {
            override fun migrate(connection: SQLiteConnection): Unit =
                with(connection) {
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
                """
                        .trimIndent()
                )

                // Create proper indices for MeasurementEntity
                execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_epochDay
            ON MeasurementEntity (epochDay)
                """
                        .trimIndent()
                )

                execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_productId
            ON MeasurementEntity (productId)
                """
                        .trimIndent()
                )

                execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_recipeId
            ON MeasurementEntity (recipeId)
                """
                        .trimIndent()
                )

                execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_MeasurementEntity_mealId
            ON MeasurementEntity (mealId)
                """
                        .trimIndent()
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
                """
                        .trimIndent()
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
                """
                        .trimIndent()
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
                """
                        .trimIndent()
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
                """
                        .trimIndent()
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
                """
                        .trimIndent()
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
                """
                        .trimIndent()
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
                """
                        .trimIndent()
                )

                // Create proper indices for RecipeIngredientEntity
                execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_RecipeIngredientEntity_recipeId 
            ON RecipeIngredientEntity (recipeId)
                """
                        .trimIndent()
                )

                execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_RecipeIngredientEntity_ingredientProductId 
            ON RecipeIngredientEntity (ingredientProductId)
                """
                        .trimIndent()
                )

                execSQL(
                    """
            CREATE INDEX IF NOT EXISTS index_RecipeIngredientEntity_ingredientRecipeId 
            ON RecipeIngredientEntity (ingredientRecipeId)
                """
                        .trimIndent()
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
                """
                        .trimIndent()
                )

                // Drop the temporary table
                execSQL("DROP TABLE IF EXISTS RecipeIngredientEntity_temp")
            }
        }

    /** Add `isLiquid` column to ProductEntity and RecipeEntity */
    val MIGRATION_20_21 =
        object : Migration(20, 21) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
            ALTER TABLE ProductEntity 
            ADD COLUMN isLiquid INTEGER NOT NULL DEFAULT 0
                """
                        .trimIndent()
                )
                connection.execSQL(
                    """
            ALTER TABLE RecipeEntity 
            ADD COLUMN isLiquid INTEGER NOT NULL DEFAULT 0
                """
                        .trimIndent()
                )
            }
        }

    /** Add `note` column to ProductEntity and RecipeEntity */
    val MIGRATION_21_22 =
        object : Migration(21, 22) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
            ALTER TABLE ProductEntity 
            ADD COLUMN note TEXT
                """
                        .trimIndent()
                )
                connection.execSQL(
                    """
            ALTER TABLE RecipeEntity 
            ADD COLUMN note TEXT
                """
                        .trimIndent()
                )
            }
        }
}
