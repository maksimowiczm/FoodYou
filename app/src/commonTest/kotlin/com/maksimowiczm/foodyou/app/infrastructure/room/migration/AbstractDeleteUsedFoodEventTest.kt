package com.maksimowiczm.foodyou.app.infrastructure.room.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import kotlin.test.assertEquals

abstract class AbstractDeleteUsedFoodEventTest {
    abstract fun getTestHelper(): MigrationTestHelper

    open fun migrate() {
        val helper = getTestHelper()

        helper.createDatabase(26).apply {
            execSQL(
                """
                INSERT INTO Product (id, name, isLiquid, sourceType)
                VALUES (1, 'Test Product', 0, 0)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO FoodEvent (type, epochSeconds, productId)
                VALUES (4, 1, 1)
                """
                    .trimIndent()
            )

            execSQL(
                """
                INSERT INTO FoodEvent (type, epochSeconds, productId)
                VALUES (1, 2, 1)
                """
                    .trimIndent()
            )
        }

        helper.runMigrationsAndValidate(27, listOf(deleteUsedFoodEvent)).use { conn ->
            conn
                .prepare(
                    """
                SELECT COUNT(*) FROM FoodEvent
                WHERE type = 4
                """
                        .trimIndent()
                )
                .use { stmt ->
                    stmt.step()
                    val count = stmt.getLong(0)
                    assertEquals(0, count)
                }
        }
    }
}
