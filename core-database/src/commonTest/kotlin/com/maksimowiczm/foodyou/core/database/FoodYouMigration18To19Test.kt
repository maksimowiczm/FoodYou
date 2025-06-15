package com.maksimowiczm.foodyou.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL

abstract class FoodYouMigration18To19Test {

    abstract fun getTestHelper(): MigrationTestHelper

    open fun migrate() {
        val helper = getTestHelper()

        helper.createDatabase(18).apply {
            execSQL(
                """
                INSERT INTO MealEntity (
                    id, name, fromHour, fromMinute, toHour, toMinute, rank
                ) VALUES (
                    1, 'Breakfast', 7, 0, 10, 0, 1
                )
                """.trimIndent()
            )

            execSQL(
                """
                INSERT INTO ProductEntity (
                    id, name, productSource, carbohydrates, proteins, fats, calories
                )
                VALUES (
                    1, 'Apple', 0, 14.0, 0.3, 0.2, 52
                )
                """.trimIndent()
            )

            execSQL(
                """
                INSERT INTO RecipeEntity (
                    id, name, servings
                )
                VALUES (
                    1, 'Apple Pie', 8
                )
                """.trimIndent()
            )

            execSQL(
                """
                INSERT INTO ProductMeasurementEntity (
                    mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
                ) VALUES (
                    1, 100, 1, 0, 1, 100, 0
                )
                """.trimIndent()
            )

            execSQL(
                """
                INSERT INTO ProductMeasurementEntity (
                    mealId, diaryEpochDay, productId, measurement, quantity, createdAt, isDeleted
                ) VALUES (
                    1, 100, 1, 0, 1, 100, 1
                )
                """.trimIndent()
            )

            execSQL(
                """
                INSERT INTO RecipeMeasurementEntity (
                    mealId, epochDay, recipeId, measurement, quantity, createdAt, isDeleted
                ) VALUES (
                    1, 100, 1, 0, 2, 100, 0
                )
                """.trimIndent()
            )

            execSQL(
                """
                INSERT INTO RecipeMeasurementEntity (
                    mealId, epochDay, recipeId, measurement, quantity, createdAt, isDeleted
                ) VALUES (
                    1, 100, 1, 0, 2, 100, 1
                )
                """.trimIndent()
            )

            close()
        }

        val conn = helper.runMigrationsAndValidate(19, listOf(MIGRATION_18_19))

        val query = """
            SELECT
                mealId,
                epochDay,
                productId,
                recipeId,
                measurement,
                quantity,
                createdAt,
                isDeleted
            FROM MeasurementEntity
        """.trimIndent()

        conn.prepare(query).use {
            assert(it.step())

            assert(it.getLong(0) == 1L)
            assert(it.getLong(1) == 100L)
            assert(it.getLong(2) == 1L)
            assert(it.isNull(3))
            assert(it.getInt(4) == 0)
            assert(it.getDouble(5) == 1.0)
            assert(it.getLong(6) == 100L)
            assert(it.getInt(7) == 0)

            assert(it.step())

            assert(it.getLong(0) == 1L)
            assert(it.getLong(1) == 100L)
            assert(it.getLong(2) == 1L)
            assert(it.isNull(3))
            assert(it.getInt(4) == 0)
            assert(it.getDouble(5) == 1.0)
            assert(it.getLong(6) == 100L)
            assert(it.getInt(7) == 1)
            assert(it.step())

            assert(it.step())

            assert(it.getLong(0) == 1L)
            assert(it.getLong(1) == 100L)
            assert(it.isNull(2))
            assert(it.getLong(3) == 1L)
            assert(it.getInt(4) == 0)
            assert(it.getDouble(5) == 2.0)
            assert(it.getLong(6) == 100L)
            assert(it.getInt(7) == 0)

            assert(it.step())

            assert(it.getLong(0) == 1L)
            assert(it.getLong(1) == 100L)
            assert(it.isNull(2))
            assert(it.getLong(3) == 1L)
            assert(it.getInt(4) == 0)
            assert(it.getDouble(5) == 2.0)
            assert(it.getLong(6) == 100L)
            assert(it.getInt(7) == 1)

            assert(!it.step()) { "Expected no more rows, but there are more." }
        }

        conn.close()
    }
}
