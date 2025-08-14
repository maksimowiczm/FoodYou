package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration.unlinkDiaryMigration

// It doesn't really check if migration works correctly, but it checks if it doesn't crash
abstract class AbstractUnlinkDiaryMigrationTest {
    abstract fun getTestHelper(): MigrationTestHelper

    open fun migrate() {
        val helper = getTestHelper()
        helper.createDatabase(25).apply {
            execSQL(
                """
                INSERT INTO Product (id, name, isLiquid, sourceType)
                VALUES (1, 'Test Product', 0, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Recipe (id, name, servings, isLiquid)
                VALUES (1, 'Depth 0 recipe', 2, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO RecipeIngredient (recipeId, ingredientProductId, measurement, quantity)
                VALUES (1, 1, 0, 100.0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Recipe (id, name, servings, isLiquid)
                VALUES (2, 'Depth 1 recipe', 2, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO RecipeIngredient (recipeId, ingredientProductId, measurement, quantity)
                VALUES (2, 1, 0, 100.0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO RecipeIngredient (recipeId, ingredientRecipeId, measurement, quantity)
                VALUES (2, 1, 0, 100.0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Meal (name, fromHour, fromMinute, toHour, toMinute, rank)
                VALUES ('Test Meal', 8, 0, 9, 0, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Measurement (mealId, epochDay, productId, recipeId, measurement, quantity, createdAt, isDeleted)
                VALUES (1, 0, 1, NULL, 0, 100.0, 0, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Measurement (mealId, epochDay, productId, recipeId, measurement, quantity, createdAt, isDeleted)
                VALUES (1, 0, NULL, 1, 0, 100.0, 0, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Measurement (mealId, epochDay, productId, recipeId, measurement, quantity, createdAt, isDeleted)
                VALUES (1, 0, NULL, 2, 0, 100.0, 0, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Measurement (mealId, epochDay, productId, recipeId, measurement, quantity, createdAt, isDeleted)
                VALUES (1, 0, NULL, 2, 0, 100.0, 0, 1)
                """
                    .trimIndent()
            )
        }
        helper.runMigrationsAndValidate(26, listOf(unlinkDiaryMigration)).close()
    }
}
