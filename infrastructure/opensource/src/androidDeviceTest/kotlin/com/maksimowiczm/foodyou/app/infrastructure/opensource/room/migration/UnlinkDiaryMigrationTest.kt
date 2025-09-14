package com.maksimowiczm.foodyou.app.infrastructure.opensource.room.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.platform.app.InstrumentationRegistry
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.FoodYouDatabase
import org.junit.Rule
import org.junit.Test

class UnlinkDiaryMigrationTest : AbstractUnlinkDiaryMigrationTest() {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val file = instrumentation.targetContext.getDatabasePath("UnlinkDiaryMigrationTest.db")
    private val driver: SQLiteDriver = AndroidSQLiteDriver()

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
