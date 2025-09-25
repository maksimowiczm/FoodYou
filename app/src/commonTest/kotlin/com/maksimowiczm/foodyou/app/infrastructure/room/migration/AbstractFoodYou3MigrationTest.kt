package com.maksimowiczm.foodyou.app.infrastructure.room.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodEventTypeSQLConstants
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class AbstractFoodYou3MigrationTest {
    abstract fun getTestHelper(): MigrationTestHelper

    open fun migrate() {
        val helper = getTestHelper()

        helper.createDatabase(22).apply {
            insertFoodYou2Product(
                id = 1,
                name = "Test Product",
                brand = null,
                barcode = null,
                packageWeight = 100f,
                servingWeight = 50f,
                isLiquid = false,
                note = "Test note",
                proteins = 5f,
                carbohydrates = 20f,
                fats = 2f,
                calories = 100f,
                saturatedFats = null,
                monounsaturatedFats = null,
                polyunsaturatedFats = null,
                omega3 = null,
                omega6 = null,
                sugars = null,
                salt = null,
                fiber = null,
                cholesterolMilli = null,
                caffeineMilli = null,
                vitaminAMicro = null,
                vitaminB1Milli = null,
                vitaminB2Milli = null,
                vitaminB3Milli = null,
                vitaminB5Milli = null,
                vitaminB6Milli = null,
                vitaminB7Micro = null,
                vitaminB9Micro = null,
                vitaminB12Micro = null,
                vitaminCMilli = null,
                vitaminDMicro = null,
                vitaminEMilli = null,
                vitaminKMicro = null,
                manganeseMilli = null,
                magnesiumMilli = null,
                potassiumMilli = null,
                calciumMilli = null,
                copperMilli = null,
                zincMilli = null,
                sodiumMilli = null,
                ironMilli = null,
                phosphorusMilli = null,
                seleniumMicro = null,
                iodineMicro = null,
                chromiumMicro = null,
            )
            execSQL(
                "INSERT INTO RecipeEntity (id, name, servings, isLiquid) VALUES (1, 'Test Recipe', 2, 0)"
            )
            execSQL(
                "INSERT INTO RecipeIngredientEntity (id, recipeId, ingredientProductId, measurement, quantity) VALUES (1, 1, 1, 1, 50.0)"
            )
            execSQL(
                "INSERT INTO MealEntity (id, name, fromHour, fromMinute, toHour, toMinute, rank) VALUES (1, 'Breakfast', 6, 0, 10, 0, 0)"
            )
            execSQL(
                "INSERT INTO MeasurementEntity (id, mealId, epochDay, productId, recipeId, measurement, quantity, createdAt, isDeleted) VALUES (1, 1, 1, 1, NULL, 1, 100.0, 12345, 0)"
            )
            execSQL(
                "INSERT INTO SearchQueryEntity (query, epochSeconds) VALUES ('test query', 12345)"
            )
            close()
        }

        val connection = helper.runMigrationsAndValidate(23, listOf(foodYou3Migration))

        // Verify that the data has been migrated correctly
        connection
            .prepare(
                """
                    SELECT id, name, brand, barcode, packageWeight, servingWeight, isLiquid, note, energy, proteins, fats, saturatedFats, transFats, monounsaturatedFats, polyunsaturatedFats, omega3, omega6, carbohydrates, sugars, addedSugars, dietaryFiber, solubleFiber, insolubleFiber, salt, cholesterolMilli, caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli, vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro, vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli, magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli, ironMilli, phosphorusMilli, seleniumMicro, iodineMicro, chromiumMicro
                    FROM Product
                """
                    .trimIndent()
            )
            .use { statement ->
                statement.step()

                assertTrue { statement.getLong(0) == 1L } // id
                assertTrue { statement.getText(1) == "Test Product" } // name
                assertTrue { statement.isNull(2) } // brand
                assertTrue { statement.isNull(3) } // barcode
                assertTrue { statement.getDouble(4) == 100.0 } // packageWeight
                assertTrue { statement.getDouble(5) == 50.0 } // servingWeight
                assertFalse { statement.getBoolean(6) } // isLiquid
                assertTrue { statement.getText(7) == "Test note" } // note
                assertTrue { statement.getDouble(8) == 100.0 } // energy
                assertTrue { statement.getDouble(9) == 5.0 } // proteins
                assertTrue { statement.getDouble(10) == 2.0 } // fats
                assertTrue { statement.isNull(11) } // saturatedFats
                assertTrue { statement.isNull(12) } // transFats
                assertTrue { statement.isNull(13) } // monounsaturatedFats
                assertTrue { statement.isNull(14) } // polyunsaturatedFats
                assertTrue { statement.isNull(15) } // omega3
                assertTrue { statement.isNull(16) } // omega6
                assertTrue { statement.getDouble(17) == 20.0 } // carbohydrates
                assertTrue { statement.isNull(18) } // sugars
                assertTrue { statement.isNull(19) } // addedSugars
                assertTrue { statement.isNull(20) } // dietaryFiber
                assertTrue { statement.isNull(21) } // solubleFiber
                assertTrue { statement.isNull(22) } // insolubleFiber
                assertTrue { statement.isNull(23) } // salt
                assertTrue { statement.isNull(24) } // cholesterolMilli
                assertTrue { statement.isNull(25) } // caffeineMilli
                assertTrue { statement.isNull(26) } // vitaminAMicro
                assertTrue { statement.isNull(27) } // vitaminB1Milli
                assertTrue { statement.isNull(28) } // vitaminB2Milli
                assertTrue { statement.isNull(29) } // vitaminB3Milli
                assertTrue { statement.isNull(30) } // vitaminB5Milli
                assertTrue { statement.isNull(31) } // vitaminB6Milli
                assertTrue { statement.isNull(32) } // vitaminB7Micro
                assertTrue { statement.isNull(33) } // vitaminB9Micro
                assertTrue { statement.isNull(34) } // vitaminB12Micro
                assertTrue { statement.isNull(35) } // vitaminCMilli
                assertTrue { statement.isNull(36) } // vitaminDMicro
                assertTrue { statement.isNull(37) } // vitaminEMilli
                assertTrue { statement.isNull(38) } // vitaminKMicro
                assertTrue { statement.isNull(39) } // manganeseMilli
                assertTrue { statement.isNull(40) } // magnesiumMilli
                assertTrue { statement.isNull(41) } // potassiumMilli
                assertTrue { statement.isNull(42) } // calciumMilli
                assertTrue { statement.isNull(43) } // copperMilli
                assertTrue { statement.isNull(44) } // zincMilli
                assertTrue { statement.isNull(45) } // sodiumMilli
                assertTrue { statement.isNull(46) } // ironMilli
                assertTrue { statement.isNull(47) } // phosphorusMilli
                assertTrue { statement.isNull(48) } // seleniumMicro
                assertTrue { statement.isNull(49) } // iodineMicro
                assertTrue { statement.isNull(50) } // chromiumMicro

                assertFalse { statement.step() }
            }

        connection.prepare("SELECT id, name, servings, isLiquid FROM Recipe").use { statement ->
            statement.step()

            assertTrue { statement.getLong(0) == 1L } // id
            assertTrue { statement.getText(1) == "Test Recipe" } // name
            assertTrue { statement.getLong(2) == 2L } // servings
            assertFalse { statement.getBoolean(3) } // isLiquid

            assertFalse { statement.step() }
        }

        connection
            .prepare(
                "SELECT id, recipeId, ingredientProductId, measurement, quantity FROM RecipeIngredient"
            )
            .use { statement ->
                statement.step()

                assertTrue { statement.getLong(0) == 1L } // id
                assertTrue { statement.getLong(1) == 1L } // recipeId
                assertTrue { statement.getLong(2) == 1L } // ingredientProductId
                assertTrue { statement.getLong(3) == 1L } // measurement
                assertTrue { statement.getDouble(4) == 50.0 } // quantity

                assertFalse { statement.step() }
            }

        connection.prepare("SELECT * FROM Meal").use { statement ->
            statement.step()

            assertTrue { statement.getLong(0) == 1L } // id
            assertTrue { statement.getText(1) == "Breakfast" } // name
            assertTrue { statement.getLong(2) == 6L } // fromHour
            assertTrue { statement.getLong(3) == 0L } // fromMinute
            assertTrue { statement.getLong(4) == 10L } // toHour
            assertTrue { statement.getLong(5) == 0L } // toMinute
            assertTrue { statement.getLong(6) == 0L } // rank

            assertFalse { statement.step() }
        }

        connection.prepare("SELECT * FROM Measurement").use { statement ->
            statement.step()

            assertTrue { statement.getLong(0) == 1L } // id
            assertTrue { statement.getLong(1) == 1L } // mealId
            assertTrue { statement.getLong(2) == 1L } // epochDay
            assertTrue { statement.getLong(3) == 1L } // productId
            assertTrue { statement.isNull(4) } // recipeId
            assertTrue { statement.getLong(5) == 1L } // measurement
            assertTrue { statement.getDouble(6) == 100.0 } // quantity
            assertTrue { statement.getLong(7) == 12345L } // createdAt
            assertFalse { statement.isNull(8) } // isDeleted

            assertFalse { statement.step() }
        }

        connection.prepare("SELECT * FROM SearchEntry").use { statement ->
            statement.step()

            assertTrue { statement.getLong(0) == 12345L } // epochSeconds
            assertTrue { statement.getText(1) == "test query" } // query

            assertFalse { statement.step() }
        }

        // Verify that food events were created
        connection
            .prepare("SELECT type, extra, productId, recipeId FROM FoodEvent WHERE productId = 1")
            .use { statement ->
                statement.step()

                assertTrue {
                    statement.getInt(0) == FoodEventTypeSQLConstants.IMPORTED_FROM_FOOD_YOU_2
                }
                assertTrue { statement.isNull(1) } // extra
                assertTrue { statement.getLong(2) == 1L } // productId
                assertTrue { statement.isNull(3) } // recipeId

                assertFalse { statement.step() }
            }
        connection
            .prepare("SELECT type, extra, productId, recipeId FROM FoodEvent WHERE recipeId = 1")
            .use { statement ->
                statement.step()

                assertTrue {
                    statement.getInt(0) == FoodEventTypeSQLConstants.IMPORTED_FROM_FOOD_YOU_2
                }
                assertTrue { statement.isNull(1) } // extra
                assertTrue { statement.isNull(2) } // productId
                assertTrue { statement.getLong(3) == 1L } // recipeId

                assertFalse { statement.step() }
            }

        // Check that the old tables no longer exist
        assertFalse(isTableExists(connection, "ProductEntity"))
        assertFalse(isTableExists(connection, "RecipeEntity"))
        assertFalse(isTableExists(connection, "RecipeIngredientEntity"))
        assertFalse(isTableExists(connection, "MealEntity"))
        assertFalse(isTableExists(connection, "MeasurementEntity"))
        assertFalse(isTableExists(connection, "SearchQueryEntity"))

        connection.close()
    }

    private fun isTableExists(connection: SQLiteConnection, tableName: String): Boolean =
        connection.prepare("SELECT name FROM sqlite_master WHERE type='table' AND name=?").use {
            statement ->
            statement.bindText(1, tableName)
            val hasRows = statement.step()

            if (!hasRows) {
                return false
            }

            val name = statement.getText(0)
            statement.close()

            return name == tableName
        }
}

private fun SQLiteConnection.insertFoodYou2Product(
    id: Long,
    name: String,
    brand: String?,
    barcode: String?,
    packageWeight: Float?,
    servingWeight: Float?,
    isLiquid: Boolean,
    note: String?,
    proteins: Float?,
    carbohydrates: Float?,
    fats: Float?,
    calories: Float?,
    saturatedFats: Float?,
    monounsaturatedFats: Float?,
    polyunsaturatedFats: Float?,
    omega3: Float?,
    omega6: Float?,
    sugars: Float?,
    salt: Float?,
    fiber: Float?,
    cholesterolMilli: Float?,
    caffeineMilli: Float?,
    vitaminAMicro: Float?,
    vitaminB1Milli: Float?,
    vitaminB2Milli: Float?,
    vitaminB3Milli: Float?,
    vitaminB5Milli: Float?,
    vitaminB6Milli: Float?,
    vitaminB7Micro: Float?,
    vitaminB9Micro: Float?,
    vitaminB12Micro: Float?,
    vitaminCMilli: Float?,
    vitaminDMicro: Float?,
    vitaminEMilli: Float?,
    vitaminKMicro: Float?,
    manganeseMilli: Float?,
    magnesiumMilli: Float?,
    potassiumMilli: Float?,
    calciumMilli: Float?,
    copperMilli: Float?,
    zincMilli: Float?,
    sodiumMilli: Float?,
    ironMilli: Float?,
    phosphorusMilli: Float?,
    seleniumMicro: Float?,
    iodineMicro: Float?,
    chromiumMicro: Float?,
) {
    val statement =
        prepare(
            """
                INSERT INTO ProductEntity (
                    id, name, brand, barcode, packageWeight, servingWeight, isLiquid, note, proteins, 
                    carbohydrates, fats, calories, saturatedFats, monounsaturatedFats, 
                    polyunsaturatedFats, omega3, omega6, sugars, salt, fiber, cholesterolMilli,
                    caffeineMilli, vitaminAMicro, vitaminB1Milli, vitaminB2Milli, vitaminB3Milli,
                    vitaminB5Milli, vitaminB6Milli, vitaminB7Micro, vitaminB9Micro, vitaminB12Micro,
                    vitaminCMilli, vitaminDMicro, vitaminEMilli, vitaminKMicro, manganeseMilli,
                    magnesiumMilli, potassiumMilli, calciumMilli, copperMilli, zincMilli, sodiumMilli,
                    ironMilli, phosphorusMilli, seleniumMicro, iodineMicro, chromiumMicro
                ) VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, 
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?
                )
            """
                .trimIndent()
        )

    statement.bindLong(1, id)
    statement.bindText(2, name)
    statement.bindTextOrNull(3, brand)
    statement.bindTextOrNull(4, barcode)
    statement.bindDoubleOrNull(5, packageWeight)
    statement.bindDoubleOrNull(6, servingWeight)
    statement.bindBoolean(7, isLiquid)
    statement.bindTextOrNull(8, note)
    statement.bindDoubleOrNull(9, proteins)
    statement.bindDoubleOrNull(10, carbohydrates)
    statement.bindDoubleOrNull(11, fats)
    statement.bindDoubleOrNull(12, calories)
    statement.bindDoubleOrNull(13, saturatedFats)
    statement.bindDoubleOrNull(14, monounsaturatedFats)
    statement.bindDoubleOrNull(15, polyunsaturatedFats)
    statement.bindDoubleOrNull(16, omega3)
    statement.bindDoubleOrNull(17, omega6)
    statement.bindDoubleOrNull(18, sugars)
    statement.bindDoubleOrNull(19, salt)
    statement.bindDoubleOrNull(20, fiber)
    statement.bindDoubleOrNull(21, cholesterolMilli)
    statement.bindDoubleOrNull(22, caffeineMilli)
    statement.bindDoubleOrNull(23, vitaminAMicro)
    statement.bindDoubleOrNull(24, vitaminB1Milli)
    statement.bindDoubleOrNull(25, vitaminB2Milli)
    statement.bindDoubleOrNull(26, vitaminB3Milli)
    statement.bindDoubleOrNull(27, vitaminB5Milli)
    statement.bindDoubleOrNull(28, vitaminB6Milli)
    statement.bindDoubleOrNull(29, vitaminB7Micro)
    statement.bindDoubleOrNull(30, vitaminB9Micro)
    statement.bindDoubleOrNull(31, vitaminB12Micro)
    statement.bindDoubleOrNull(32, vitaminCMilli)
    statement.bindDoubleOrNull(33, vitaminDMicro)
    statement.bindDoubleOrNull(34, vitaminEMilli)
    statement.bindDoubleOrNull(35, vitaminKMicro)
    statement.bindDoubleOrNull(36, manganeseMilli)
    statement.bindDoubleOrNull(37, magnesiumMilli)
    statement.bindDoubleOrNull(38, potassiumMilli)
    statement.bindDoubleOrNull(39, calciumMilli)
    statement.bindDoubleOrNull(40, copperMilli)
    statement.bindDoubleOrNull(41, zincMilli)
    statement.bindDoubleOrNull(42, sodiumMilli)
    statement.bindDoubleOrNull(43, ironMilli)
    statement.bindDoubleOrNull(44, phosphorusMilli)
    statement.bindDoubleOrNull(45, seleniumMicro)
    statement.bindDoubleOrNull(46, iodineMicro)
    statement.bindDoubleOrNull(47, chromiumMicro)
    statement.step()
    statement.close()
}

private fun SQLiteStatement.bindDoubleOrNull(index: Int, value: Float?) {
    if (value != null) {
        bindDouble(index, value.toDouble())
    } else {
        bindNull(index)
    }
}

private fun SQLiteStatement.bindTextOrNull(index: Int, value: String?) {
    if (value != null) {
        bindText(index, value)
    } else {
        bindNull(index)
    }
}
