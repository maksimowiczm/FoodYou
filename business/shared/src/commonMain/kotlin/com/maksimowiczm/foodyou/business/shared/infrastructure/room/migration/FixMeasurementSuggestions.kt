package com.maksimowiczm.foodyou.business.shared.infrastructure.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/** Add foreign keys to MeasurementSuggestion table. */
internal val fixMeasurementSuggestions =
    object : Migration(27, 28) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("DROP TABLE IF EXISTS MeasurementSuggestion")
            connection.execSQL(
                """
            CREATE TABLE MeasurementSuggestion (
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                productId INTEGER,
                recipeId INTEGER,
                type INTEGER NOT NULL,
                value REAL NOT NULL,
                epochSeconds INTEGER NOT NULL,
                FOREIGN KEY (productId) REFERENCES Product(id) ON DELETE CASCADE,
                FOREIGN KEY (recipeId) REFERENCES Recipe(id) ON DELETE CASCADE
            )
            """
                    .trimIndent()
            )
        }
    }
