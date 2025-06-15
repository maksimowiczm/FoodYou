package com.maksimowiczm.foodyou.core.database

import androidx.core.database.getIntOrNull
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.jvm.java
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("className")
@RunWith(AndroidJUnit4::class)
class FoodYouMigration18To19Test {
    private companion object {
        private const val TEST_DB_NAME = "migration_18_19.db"
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = FoodYouDatabase::class.java,
        specs = emptyList(),
        openFactory = FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate() {
        var db = helper.createDatabase(
            name = TEST_DB_NAME,
            version = 18
        ).apply {
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

        db = helper.runMigrationsAndValidate(
            name = TEST_DB_NAME,
            version = 19,
            validateDroppedTables = true,
            MIGRATION_18_19
        )

        db.query("SELECT * FROM MeasurementEntity").use { cursor ->
            assert(cursor.count == 4) { "Expected 3 measurements, found ${cursor.count}" }

            cursor.moveToFirst()

            assert(cursor.getLong(cursor.getColumnIndex("mealId")) == 1L)
            assert(cursor.getLong(cursor.getColumnIndex("epochDay")) == 100L)
            assert(cursor.getLong(cursor.getColumnIndex("productId")) == 1L)
            assert(cursor.getIntOrNull(cursor.getColumnIndex("recipeId")) == null)
            assert(cursor.getInt(cursor.getColumnIndex("measurement")) == 0)
            assert(cursor.getDouble(cursor.getColumnIndex("quantity")) == 1.0)
            assert(cursor.getLong(cursor.getColumnIndex("createdAt")) == 100L)
            assert(cursor.getInt(cursor.getColumnIndex("isDeleted")) == 0)

            cursor.moveToNext()

            assert(cursor.getLong(cursor.getColumnIndex("mealId")) == 1L)
            assert(cursor.getLong(cursor.getColumnIndex("epochDay")) == 100L)
            assert(cursor.getLong(cursor.getColumnIndex("productId")) == 1L)
            assert(cursor.getIntOrNull(cursor.getColumnIndex("recipeId")) == null)
            assert(cursor.getInt(cursor.getColumnIndex("measurement")) == 0)
            assert(cursor.getDouble(cursor.getColumnIndex("quantity")) == 1.0)
            assert(cursor.getLong(cursor.getColumnIndex("createdAt")) == 100L)
            assert(cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1)

            cursor.moveToNext()

            assert(cursor.getLong(cursor.getColumnIndex("mealId")) == 1L)
            assert(cursor.getLong(cursor.getColumnIndex("epochDay")) == 100L)
            assert(cursor.getIntOrNull(cursor.getColumnIndex("productId")) == null)
            assert(cursor.getLong(cursor.getColumnIndex("recipeId")) == 1L)
            assert(cursor.getInt(cursor.getColumnIndex("measurement")) == 0)
            assert(cursor.getDouble(cursor.getColumnIndex("quantity")) == 2.0)
            assert(cursor.getLong(cursor.getColumnIndex("createdAt")) == 100L)
            assert(cursor.getInt(cursor.getColumnIndex("isDeleted")) == 0)

            cursor.moveToNext()

            assert(cursor.getLong(cursor.getColumnIndex("mealId")) == 1L)
            assert(cursor.getLong(cursor.getColumnIndex("epochDay")) == 100L)
            assert(cursor.getIntOrNull(cursor.getColumnIndex("productId")) == null)
            assert(cursor.getLong(cursor.getColumnIndex("recipeId")) == 1L)
            assert(cursor.getInt(cursor.getColumnIndex("measurement")) == 0)
            assert(cursor.getDouble(cursor.getColumnIndex("quantity")) == 2.0)
            assert(cursor.getLong(cursor.getColumnIndex("createdAt")) == 100L)
            assert(cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1)

            assert(cursor.isLast) { "Expected cursor to be at last position, but it is not." }

            cursor.close()
        }
    }
}
