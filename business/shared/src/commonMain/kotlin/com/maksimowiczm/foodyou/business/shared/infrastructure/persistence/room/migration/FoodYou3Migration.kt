package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventTypeSQLConstants
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Migration from FoodYou 2.10 to FoodYou 3.0 */
@OptIn(ExperimentalTime::class)
val foodYou3Migration =
    object : Migration(22, 23) {
        override fun migrate(connection: SQLiteConnection) {
            // Step 1: Create the new Sponsorship table
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Sponsorship` (
                `id` INTEGER NOT NULL,
                `sponsorName` TEXT,
                `message` TEXT,
                `amount` TEXT NOT NULL,
                `currency` TEXT NOT NULL,
                `inEuro` TEXT NOT NULL,
                `sponsorshipEpochSeconds` INTEGER NOT NULL,
                `method` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """
                    .trimIndent()
            )

            // Create index for Sponsorship table
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_Sponsorship_sponsorshipEpochSeconds`
            ON `Sponsorship` (`sponsorshipEpochSeconds`)
            """
                    .trimIndent()
            )

            // Step 2: Create new Product table with updated structure
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Product` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `brand` TEXT,
                `barcode` TEXT,
                `packageWeight` REAL,
                `servingWeight` REAL,
                `note` TEXT,
                `sourceType` INTEGER NOT NULL DEFAULT 0,
                `sourceUrl` TEXT,
                `isLiquid` INTEGER NOT NULL,
                `energy` REAL,
                `proteins` REAL,
                `fats` REAL,
                `saturatedFats` REAL,
                `transFats` REAL,
                `monounsaturatedFats` REAL,
                `polyunsaturatedFats` REAL,
                `omega3` REAL,
                `omega6` REAL,
                `carbohydrates` REAL,
                `sugars` REAL,
                `addedSugars` REAL,
                `dietaryFiber` REAL,
                `solubleFiber` REAL,
                `insolubleFiber` REAL,
                `salt` REAL,
                `cholesterolMilli` REAL,
                `caffeineMilli` REAL,
                `vitaminAMicro` REAL,
                `vitaminB1Milli` REAL,
                `vitaminB2Milli` REAL,
                `vitaminB3Milli` REAL,
                `vitaminB5Milli` REAL,
                `vitaminB6Milli` REAL,
                `vitaminB7Micro` REAL,
                `vitaminB9Micro` REAL,
                `vitaminB12Micro` REAL,
                `vitaminCMilli` REAL,
                `vitaminDMicro` REAL,
                `vitaminEMilli` REAL,
                `vitaminKMicro` REAL,
                `manganeseMilli` REAL,
                `magnesiumMilli` REAL,
                `potassiumMilli` REAL,
                `calciumMilli` REAL,
                `copperMilli` REAL,
                `zincMilli` REAL,
                `sodiumMilli` REAL,
                `ironMilli` REAL,
                `phosphorusMilli` REAL,
                `seleniumMicro` REAL,
                `iodineMicro` REAL,
                `chromiumMicro` REAL
            )
            """
                    .trimIndent()
            )

            // Step 3: Migrate data from ProductEntity to Product
            connection.execSQL(
                """
            INSERT INTO `Product` (
                `id`, `name`, `brand`, `barcode`, `packageWeight`, `servingWeight`, `note`,
                `sourceType`, `sourceUrl`, `isLiquid`, `energy`, `proteins`, `fats`,
                `saturatedFats`, `transFats`, `monounsaturatedFats`, `polyunsaturatedFats`,
                `omega3`, `omega6`, `carbohydrates`, `sugars`, `addedSugars`,
                `dietaryFiber`, `solubleFiber`, `insolubleFiber`, `salt`,
                `cholesterolMilli`, `caffeineMilli`, `vitaminAMicro`, `vitaminB1Milli`,
                `vitaminB2Milli`, `vitaminB3Milli`, `vitaminB5Milli`, `vitaminB6Milli`,
                `vitaminB7Micro`, `vitaminB9Micro`, `vitaminB12Micro`, `vitaminCMilli`,
                `vitaminDMicro`, `vitaminEMilli`, `vitaminKMicro`, `manganeseMilli`,
                `magnesiumMilli`, `potassiumMilli`, `calciumMilli`, `copperMilli`,
                `zincMilli`, `sodiumMilli`, `ironMilli`, `phosphorusMilli`,
                `seleniumMicro`, `iodineMicro`, `chromiumMicro`
            )
            SELECT
                `id`, `name`,
                CASE WHEN `brand` = '' THEN NULL ELSE `brand` END,
                CASE WHEN `barcode` = '' THEN NULL ELSE `barcode` END,
                `packageWeight`, `servingWeight`, `note`,
                0 as `sourceType`, NULL as `sourceUrl`, `isLiquid`, `calories` as `energy`,
                `proteins`, `fats`, `saturatedFats`, NULL as `transFats`, `monounsaturatedFats`,
                `polyunsaturatedFats`, `omega3`, `omega6`, `carbohydrates`, `sugars`,
                NULL as `addedSugars`, `fiber` as `dietaryFiber`, NULL as `solubleFiber`,
                NULL as `insolubleFiber`, `salt`, `cholesterolMilli`, `caffeineMilli`,
                `vitaminAMicro`, `vitaminB1Milli`, `vitaminB2Milli`, `vitaminB3Milli`,
                `vitaminB5Milli`, `vitaminB6Milli`, `vitaminB7Micro`, `vitaminB9Micro`,
                `vitaminB12Micro`, `vitaminCMilli`, `vitaminDMicro`, `vitaminEMilli`,
                `vitaminKMicro`, `manganeseMilli`, `magnesiumMilli`, `potassiumMilli`,
                `calciumMilli`, `copperMilli`, `zincMilli`, `sodiumMilli`, `ironMilli`,
                `phosphorusMilli`, `seleniumMicro`, `iodineMicro`, `chromiumMicro`
            FROM `ProductEntity`
            """
                    .trimIndent()
            )

            // Step 4: Create new Recipe table
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Recipe` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `servings` INTEGER NOT NULL,
                `note` TEXT,
                `isLiquid` INTEGER NOT NULL
            )
            """
                    .trimIndent()
            )

            // Step 5: Migrate data from RecipeEntity to Recipe
            connection.execSQL(
                """
            INSERT INTO `Recipe` (`id`, `name`, `servings`, `note`, `isLiquid`)
            SELECT
                `id`,
                `name`,
                `servings`,
                CASE WHEN `note` = '' THEN NULL ELSE `note` END,
                `isLiquid`
            FROM `RecipeEntity`
            """
                    .trimIndent()
            )

            // Step 6: Create new RecipeIngredient table
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `RecipeIngredient` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `recipeId` INTEGER NOT NULL,
                `ingredientProductId` INTEGER,
                `ingredientRecipeId` INTEGER,
                `measurement` INTEGER NOT NULL,
                `quantity` REAL NOT NULL,
                FOREIGN KEY(`recipeId`) REFERENCES `Recipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`ingredientProductId`) REFERENCES `Product`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`ingredientRecipeId`) REFERENCES `Recipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """
                    .trimIndent()
            )

            // Create indices for RecipeIngredient
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_RecipeIngredient_recipeId`
            ON `RecipeIngredient` (`recipeId`)
            """
                    .trimIndent()
            )
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_RecipeIngredient_ingredientProductId`
            ON `RecipeIngredient` (`ingredientProductId`)
            """
                    .trimIndent()
            )
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_RecipeIngredient_ingredientRecipeId`
            ON `RecipeIngredient` (`ingredientRecipeId`)
            """
                    .trimIndent()
            )

            // Step 7: Migrate data from RecipeIngredientEntity to RecipeIngredient
            connection.execSQL(
                """
            INSERT INTO `RecipeIngredient` (
                `id`, `recipeId`, `ingredientProductId`, `ingredientRecipeId`,
                `measurement`, `quantity`
            )
            SELECT
                `id`, `recipeId`, `ingredientProductId`, `ingredientRecipeId`,
                `measurement`, `quantity`
            FROM `RecipeIngredientEntity`
            """
                    .trimIndent()
            )

            // Step 8: Create new Meal table
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Meal` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `fromHour` INTEGER NOT NULL,
                `fromMinute` INTEGER NOT NULL,
                `toHour` INTEGER NOT NULL,
                `toMinute` INTEGER NOT NULL,
                `rank` INTEGER NOT NULL
            )
            """
                    .trimIndent()
            )

            // Step 9: Migrate data from MealEntity to Meal
            connection.execSQL(
                """
            INSERT INTO `Meal` (
                `id`, `name`, `fromHour`, `fromMinute`, `toHour`, `toMinute`, `rank`
            )
            SELECT
                `id`, `name`, `fromHour`, `fromMinute`, `toHour`, `toMinute`, `rank`
            FROM `MealEntity`
            """
                    .trimIndent()
            )

            // Step 10: Create new Measurement table
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Measurement` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `mealId` INTEGER NOT NULL,
                `epochDay` INTEGER NOT NULL,
                `productId` INTEGER,
                `recipeId` INTEGER,
                `measurement` INTEGER NOT NULL,
                `quantity` REAL NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `isDeleted` INTEGER NOT NULL,
                FOREIGN KEY(`mealId`) REFERENCES `Meal`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`productId`) REFERENCES `Product`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`recipeId`) REFERENCES `Recipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """
                    .trimIndent()
            )

            // Create indices for Measurement
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_Measurement_mealId`
            ON `Measurement` (`mealId`)
            """
                    .trimIndent()
            )
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_Measurement_epochDay`
            ON `Measurement` (`epochDay`)
            """
                    .trimIndent()
            )
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_Measurement_productId`
            ON `Measurement` (`productId`)
            """
                    .trimIndent()
            )
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_Measurement_recipeId`
            ON `Measurement` (`recipeId`)
            """
                    .trimIndent()
            )

            // Step 11: Migrate data from MeasurementEntity to Measurement
            connection.execSQL(
                """
            INSERT INTO `Measurement` (
                `id`, `mealId`, `epochDay`, `productId`, `recipeId`,
                `measurement`, `quantity`, `createdAt`, `isDeleted`
            )
            SELECT
                `id`, `mealId`, `epochDay`, `productId`, `recipeId`,
                `measurement`, `quantity`, `createdAt`, `isDeleted`
            FROM `MeasurementEntity`
            """
                    .trimIndent()
            )

            // Step 12: Create new paging and caching tables
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `OpenFoodFactsPagingKey` (
                `queryString` TEXT NOT NULL,
                `country` TEXT NOT NULL,
                `fetchedCount` INTEGER NOT NULL,
                `totalCount` INTEGER NOT NULL,
                PRIMARY KEY(`queryString`, `country`)
            )
            """
                    .trimIndent()
            )

            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `SearchEntry` (
                `epochSeconds` INTEGER NOT NULL,
                `query` TEXT NOT NULL,
                PRIMARY KEY(`query`)
            )
            """
                    .trimIndent()
            )

            // Create index for SearchEntry
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_SearchEntry_epochSeconds`
            ON `SearchEntry` (`epochSeconds`)
            """
                    .trimIndent()
            )

            // Step 13: Migrate SearchQueryEntity to SearchEntry
            connection.execSQL(
                """
            INSERT INTO `SearchEntry` (`epochSeconds`, `query`)
            SELECT `epochSeconds`, `query`
            FROM `SearchQueryEntity`
            """
                    .trimIndent()
            )

            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `USDAPagingKey` (
                `queryString` TEXT NOT NULL,
                `fetchedCount` INTEGER NOT NULL,
                `totalCount` INTEGER NOT NULL,
                PRIMARY KEY(`queryString`)
            )
            """
                    .trimIndent()
            )

            // Step 14: Create FoodEvent table
            connection.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `FoodEvent` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `type` INTEGER NOT NULL,
                `epochSeconds` INTEGER NOT NULL,
                `extra` TEXT,
                `productId` INTEGER,
                `recipeId` INTEGER,
                FOREIGN KEY(`productId`) REFERENCES `Product`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
                FOREIGN KEY(`recipeId`) REFERENCES `Recipe`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
            )
            """
                    .trimIndent()
            )

            // Create indices for FoodEvent
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_FoodEvent_productId`
            ON `FoodEvent` (`productId`)
            """
                    .trimIndent()
            )
            connection.execSQL(
                """
            CREATE INDEX IF NOT EXISTS `index_FoodEvent_recipeId`
            ON `FoodEvent` (`recipeId`)
            """
                    .trimIndent()
            )

            // Step 15: Insert ImportedFromFoodYou2 events for all migrated products
            val currentTimeSeconds = Clock.System.now().epochSeconds
            connection.execSQL(
                """
            INSERT INTO `FoodEvent` (`type`, `epochSeconds`, `extra`, `productId`, `recipeId`)
            SELECT
                ${FoodEventTypeSQLConstants.IMPORTED_FROM_FOOD_YOU_2} as `type`,
                $currentTimeSeconds as `epochSeconds`,
                NULL as `extra`,
                `id` as `productId`,
                NULL as `recipeId`
            FROM `Product`
            """
                    .trimIndent()
            )

            // Step 16: Insert ImportedFromFoodYou2 events for all migrated recipes
            connection.execSQL(
                """
            INSERT INTO `FoodEvent` (`type`, `epochSeconds`, `extra`, `productId`, `recipeId`)
            SELECT
                ${FoodEventTypeSQLConstants.IMPORTED_FROM_FOOD_YOU_2} as `type`,
                $currentTimeSeconds as `epochSeconds`,
                NULL as `extra`,
                NULL as `productId`,
                `id` as `recipeId`
            FROM `Recipe`
            """
                    .trimIndent()
            )

            // Step 17: Create the RecipeAllIngredientsView
            // Copy from 23.json because room is very picky about the view definition
            connection.execSQL(
                "CREATE VIEW `RecipeAllIngredientsView` AS WITH RECURSIVE recipeIngredients AS (\n        -- Base case: Direct ingredients of all recipes\n        SELECT \n            ri.recipeId AS targetRecipeId,\n            ri.recipeId AS parentRecipeId,\n            ri.ingredientProductId AS productId,\n            ri.ingredientRecipeId AS recipeId,\n            ri.measurement,\n            ri.quantity,\n            1 AS depthLevel\n        FROM RecipeIngredient ri\n        \n        UNION ALL\n        \n        -- Recursive case: Ingredients of sub-recipes\n        SELECT \n            prev.targetRecipeId,\n            subRi.recipeId AS parentRecipeId,\n            subRi.ingredientProductId AS productId,\n            subRi.ingredientRecipeId AS recipeId,\n            subRi.measurement,\n            subRi.quantity,\n            prev.depthLevel + 1 AS depthLevel\n        FROM RecipeIngredient subRi\n        INNER JOIN recipeIngredients prev ON subRi.recipeId = prev.recipeId\n        WHERE prev.recipeId IS NOT NULL\n    )\n    SELECT DISTINCT\n        targetRecipeId,\n        COALESCE(productId, recipeId) AS ingredientId\n    FROM recipeIngredients"
            )

            // Step 18: Drop old tables
            connection.execSQL("DROP TABLE IF EXISTS `ProductEntity`")
            connection.execSQL("DROP TABLE IF EXISTS `RecipeEntity`")
            connection.execSQL("DROP TABLE IF EXISTS `RecipeIngredientEntity`")
            connection.execSQL("DROP TABLE IF EXISTS `MealEntity`")
            connection.execSQL("DROP TABLE IF EXISTS `MeasurementEntity`")
            connection.execSQL("DROP TABLE IF EXISTS `SearchQueryEntity`")
        }
    }
