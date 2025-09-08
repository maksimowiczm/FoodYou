package com.maksimowiczm.foodyou.app.infrastructure.room.migration

import androidx.core.database.getLongOrNull
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.food.RecipeIngredientEntity
import com.maksimowiczm.foodyou.app.infrastructure.room.shared.MeasurementTypeConverter

internal actual val unlinkDiaryMigration =
    object : Migration(25, 26) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create new Measurement table with updated structure
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `Measurement_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `mealId` INTEGER NOT NULL, 
                `epochDay` INTEGER NOT NULL, 
                `productId` INTEGER, 
                `recipeId` INTEGER, 
                `measurement` INTEGER NOT NULL, 
                `quantity` REAL NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                `updatedAt` INTEGER NOT NULL, 
                FOREIGN KEY(`mealId`) REFERENCES `Meal`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """
            )

            // Create new DiaryProduct table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `DiaryProduct` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `name` TEXT NOT NULL, 
                `packageWeight` REAL, 
                `servingWeight` REAL, 
                `isLiquid` INTEGER NOT NULL, 
                `sourceType` INTEGER NOT NULL, 
                `sourceUrl` TEXT, 
                `note` TEXT, 
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
            )

            // Create new DiaryRecipe table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `DiaryRecipe` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `name` TEXT NOT NULL, 
                `servings` INTEGER NOT NULL, 
                `isLiquid` INTEGER NOT NULL, 
                `note` TEXT
            )
        """
            )

            // Create new DiaryRecipeIngredient table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `DiaryRecipeIngredient` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `recipeId` INTEGER NOT NULL, 
                `ingredientProductId` INTEGER, 
                `ingredientRecipeId` INTEGER, 
                `measurement` INTEGER NOT NULL, 
                `quantity` REAL NOT NULL, 
                FOREIGN KEY(`recipeId`) REFERENCES `DiaryRecipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, 
                FOREIGN KEY(`ingredientProductId`) REFERENCES `DiaryProduct`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, 
                FOREIGN KEY(`ingredientRecipeId`) REFERENCES `DiaryRecipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """
            )

            // Create indices for DiaryRecipeIngredient
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_DiaryRecipeIngredient_recipeId` ON `DiaryRecipeIngredient` (`recipeId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_DiaryRecipeIngredient_ingredientProductId` ON `DiaryRecipeIngredient` (`ingredientProductId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_DiaryRecipeIngredient_ingredientRecipeId` ON `DiaryRecipeIngredient` (`ingredientRecipeId`)"
            )

            // Create new MeasurementSuggestion table
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS `MeasurementSuggestion` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `productId` INTEGER, 
                `recipeId` INTEGER, 
                `type` INTEGER NOT NULL, 
                `value` REAL NOT NULL, 
                `epochSeconds` INTEGER NOT NULL
            )
        """
            )

            // Copy food data from main database to diary tables

            db.execSQL("DELETE FROM Measurement WHERE isDeleted = 1")

            db.copyMeasurements()

            // Update FoodEvent table - clean up data
            db.execSQL("UPDATE FoodEvent SET extra = NULL WHERE type = 3")

            // Delete FoodEvents that reference non-existent products/recipes
            db.execSQL(
                """
            DELETE FROM FoodEvent 
            WHERE (productId IS NOT NULL AND productId NOT IN (SELECT id FROM Product))
               OR (recipeId IS NOT NULL AND recipeId NOT IN (SELECT id FROM Recipe))
        """
            )

            // Drop old views and create new ones
            db.execSQL("DROP VIEW IF EXISTS `LatestFoodMeasuredEventView`")

            // Create new LatestMeasurementSuggestion view
            db.execSQL(
                """CREATE VIEW `LatestMeasurementSuggestion` AS SELECT id, productId, recipeId, type, value, epochSeconds
        FROM (
            SELECT 
                ms.*,
                ROW_NUMBER() OVER (
                    PARTITION BY productId, recipeId
                    ORDER BY epochSeconds DESC
                ) AS rn
            FROM MeasurementSuggestion AS ms
        )
        WHERE rn = 1"""
            )

            // Step 10: Replace old Measurement table with new one
            db.execSQL("DROP TABLE `Measurement`")
            db.execSQL("ALTER TABLE `Measurement_new` RENAME TO `Measurement`")

            // Recreate indices on the renamed table (they don't transfer automatically)
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_Measurement_mealId` ON `Measurement` (`mealId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_Measurement_epochDay` ON `Measurement` (`epochDay`)"
            )
        }
    }

private fun SupportSQLiteDatabase.copyMeasurements() {
    query(
            "SELECT mealId, epochDay, productId, recipeId, measurement, quantity, createdAt FROM Measurement"
        )
        .use {
            while (it.moveToNext()) {
                // Extract values from the current row

                val mealId = it.getLong(0)
                val epochDay = it.getLong(1)
                val recipeId = it.getLongOrNull(3)
                val productId = it.getLongOrNull(2)
                val measurement = it.getInt(4)
                val quantity = it.getDouble(5)
                val createdAt = it.getLong(6)

                if (productId == null && recipeId == null) {
                    error("Measurement must have either productId or recipeId set")
                } else if (productId != null && recipeId != null) {
                    error("Measurement cannot have both productId and recipeId set")
                }

                // Insert food into diary food tables
                val newProductId = productId?.let(::copyProduct)
                val newRecipeId = recipeId?.let(::copyRecipe)

                // Insert into DiaryMeasurement
                execSQL(
                    """
                    INSERT INTO Measurement_new (mealId, epochDay, productId, recipeId, measurement, quantity, createdAt, updatedAt)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """
                        .trimIndent(),
                    arrayOf<Any?>(
                        mealId,
                        epochDay,
                        newProductId,
                        newRecipeId,
                        measurement,
                        quantity,
                        createdAt,
                        createdAt,
                    ),
                )
            }
        }
}

private val measurementTypeConverter by lazy { MeasurementTypeConverter() }

private fun SupportSQLiteDatabase.copyProduct(productId: Long): Long =
    query(
            """
INSERT INTO DiaryProduct (name, packageWeight, servingWeight, isLiquid, sourceType, sourceUrl, note, energy, proteins, fats, saturatedFats, transFats, monounsaturatedFats, polyunsaturatedFats, omega3, omega6, carbohydrates, sugars, addedSugars, dietaryFiber, solubleFiber, insolubleFiber, salt, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, iodineMicro, chromiumMicro)
SELECT name, packageWeight, servingWeight, isLiquid, sourceType, sourceUrl, note, energy, proteins, fats, saturatedFats, transFats, monounsaturatedFats, polyunsaturatedFats, omega3, omega6, carbohydrates, sugars, addedSugars, dietaryFiber, solubleFiber, insolubleFiber, salt, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, iodineMicro, chromiumMicro
FROM Product
WHERE id = ?
RETURNING id
""",
            arrayOf<Any?>(productId),
        )
        .use {
            it.moveToNext()
            it.getLong(0)
        }

private fun SupportSQLiteDatabase.copyRecipe(recipeId: Long): Long {
    // We have to copy recursively, so we need to handle ingredients

    val newRecipeId =
        query(
                """
        INSERT INTO DiaryRecipe (name, servings, isLiquid,  note)
        SELECT name, servings, isLiquid, note
        FROM Recipe
        WHERE id = ?
        RETURNING id
    """,
                arrayOf<Any?>(recipeId),
            )
            .use {
                it.moveToNext()
                it.getLong(0)
            }

    // Copy ingredients
    val ingredientsIds =
        query(
                """
        SELECT ingredientProductId, ingredientRecipeId, measurement, quantity
        FROM RecipeIngredient
        WHERE recipeId = ?
    """,
                arrayOf<Any?>(recipeId),
            )
            .use {
                val ingredients = mutableListOf<RecipeIngredientEntity>()
                while (it.moveToNext()) {
                    val productId = it.getLongOrNull(0)
                    val recipeIngredientId = it.getLongOrNull(1)
                    val measurement = measurementTypeConverter.toWeightMeasurementType(it.getInt(2))
                    val quantity = it.getDouble(3)
                    ingredients.add(
                        RecipeIngredientEntity(
                            id = 0,
                            ingredientProductId = productId,
                            ingredientRecipeId = recipeIngredientId,
                            measurement = measurement,
                            quantity = quantity,
                        )
                    )
                }

                ingredients
            }

    // Copy each ingredient
    ingredientsIds.forEach { newEntity ->
        val newProductId =
            newEntity.ingredientProductId?.let { productId -> copyProduct(productId) }
        val newRecipeIngredientId =
            newEntity.ingredientRecipeId?.let { ingredientId -> copyRecipe(ingredientId) }

        execSQL(
            """
            INSERT INTO DiaryRecipeIngredient (recipeId, ingredientProductId, ingredientRecipeId, measurement, quantity)
            VALUES (?, ?, ?, ?, ?)
        """,
            arrayOf<Any?>(
                newRecipeId,
                newProductId,
                newRecipeIngredientId,
                measurementTypeConverter.fromWeightMeasurementType(newEntity.measurement),
                newEntity.quantity,
            ),
        )
    }

    return newRecipeId
}
