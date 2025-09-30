package com.maksimowiczm.foodyou.app.infrastructure.room.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import kotlin.test.assertEquals

abstract class AbstractFoodSearchFtsMigrationTest {
    abstract fun getTestHelper(): MigrationTestHelper

    open fun migrate() {
        val helper = getTestHelper()
        helper.createDatabase(30).apply {
            execSQL(
                """
                INSERT INTO Product (id, name, isLiquid, sourceType, barcode, brand)
                VALUES (1191, 'Organic Whole Homogenized Milk', 0, 1, '0071203100088', 'Umpqua')
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO Recipe (id, name, servings, note, isLiquid)
                VALUES (1, 'Pancakes', 4, 'Use fresh ingredients', 0)
                """
                    .trimIndent()
            )
        }

        helper.runMigrationsAndValidate(31, listOf(FoodSearchFtsMigration)).use {
            it.prepare("SELECT rowid, name FROM ProductFts WHERE ProductFts MATCH 'umpqua'").use {
                stmt ->
                stmt.step()
                val id = stmt.getLong(0)
                val name = stmt.getText(1)
                assertEquals(1191, id, "Should find product by id")
                assertEquals("Organic Whole Homogenized Milk", name, "Should find product by name")
            }
            it.prepare("SELECT rowid, name FROM RecipeFts WHERE RecipeFts MATCH 'pancakes'").use {
                stmt ->
                stmt.step()
                val id = stmt.getLong(0)
                val name = stmt.getText(1)
                assertEquals(1, id, "Should find recipe by id")
                assertEquals("Pancakes", name, "Should find recipe by name")
            }
        }
    }
}
