package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * This is a fix for [unlinkDiaryMigration] that didn't delete food events of type 4. Which caused
 * the app to crash when user tried to log food that was previously logged in the diary.
 */
internal val deleteUsedFoodEvent =
    object : Migration(26, 27) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                DELETE FROM FoodEvent
                WHERE type = 4
                """
                    .trimIndent()
            )
        }
    }
