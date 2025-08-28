package com.maksimowiczm.foodyou.business.shared.infrastructure.room

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class DeleteUsedFoodEventTest : AbstractDeleteUsedFoodEventTest() {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val file = instrumentation.targetContext.getDatabasePath("DeleteUsedFoodEventTest.db")
    private val driver: SQLiteDriver = BundledSQLiteDriver()

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            instrumentation = instrumentation,
            file = file,
            driver = driver,
            databaseClass = FoodYouDatabase::class,
        )

    override fun getTestHelper() = helper

    @Test
    override fun migrate() {
        super.migrate()
    }
}
