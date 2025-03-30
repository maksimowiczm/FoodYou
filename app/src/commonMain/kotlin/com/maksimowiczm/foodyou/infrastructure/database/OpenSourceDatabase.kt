package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.callback.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.diary.database.converter.ProductSourceConverter
import com.maksimowiczm.foodyou.feature.diary.database.converter.WeightMeasurementTypeConverter
import com.maksimowiczm.foodyou.feature.diary.database.converter.WeightUnitConverter
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.WeightMeasurementEntity

@Database(
    entities = [
        OpenFoodFactsPagingKey::class,
        ProductEntity::class,
        WeightMeasurementEntity::class,
        ProductQueryEntity::class,
        MealEntity::class
    ],
    version = OpenSourceDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(
    WeightUnitConverter::class,
    ProductSourceConverter::class,
    WeightMeasurementTypeConverter::class
)
abstract class OpenSourceDatabase :
    RoomDatabase(),
    DiaryDatabase {
    companion object {
        const val VERSION = 3

        private val migrations: List<Migration> = listOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )

        fun Builder<OpenSourceDatabase>.buildDatabase(
            initializeMealsCallback: InitializeMealsCallback
        ): OpenSourceDatabase {
            migrations.forEach(::addMigrations)
            addCallback(initializeMealsCallback)
            return build()
        }
    }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE MealEntity 
            ADD COLUMN rank INTEGER NOT NULL DEFAULT -1
            """.trimIndent()
        )
        connection.execSQL(
            """
            UPDATE MealEntity 
            SET rank = id
            """.trimIndent()
        )
    }
}

// Copy version 2 OpenFoodFactsPagingKey table to a temporary table
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        // Create a temporary table with the old schema
        connection.execSQL(
            """
            CREATE TABLE OpenFoodFactsPagingKey_temp (
                queryString TEXT NOT NULL,
                country TEXT NOT NULL,
                fetchedCount INTEGER NOT NULL,
                totalCount INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Copy data from the old OpenFoodFactsPagingKey table to the temporary table
        connection.execSQL(
            """
            INSERT INTO OpenFoodFactsPagingKey_temp (queryString, country, fetchedCount, totalCount)
            SELECT queryString, country, fetchedCount, totalCount
            FROM OpenFoodFactsPagingKey
            """.trimIndent()
        )

        // Drop the old OpenFoodFactsPagingKey table
        connection.execSQL(
            """
            DROP TABLE OpenFoodFactsPagingKey
            """.trimIndent()
        )

        // Create a new OpenFoodFactsPagingKey table with the new schema
        connection.execSQL(
            """
            CREATE TABLE OpenFoodFactsPagingKey (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                queryString TEXT NOT NULL,
                country TEXT,
                fetchedCount INTEGER NOT NULL,
                totalCount INTEGER NOT NULL,
                UNIQUE(queryString, country)
            )
            """.trimIndent()
        )
        connection.execSQL(
            """
            CREATE UNIQUE INDEX index_OpenFoodFactsPagingKey_queryString_country
            ON OpenFoodFactsPagingKey (queryString, country)
            """.trimIndent()
        )

        // Copy data from the temporary table back to the new OpenFoodFactsPagingKey table
        connection.execSQL(
            """
            INSERT INTO OpenFoodFactsPagingKey (queryString, country, fetchedCount, totalCount)
            SELECT queryString, country, fetchedCount, totalCount
            FROM OpenFoodFactsPagingKey_temp
            """.trimIndent()
        )

        // Drop the temporary table
        connection.execSQL(
            """
            DROP TABLE OpenFoodFactsPagingKey_temp
            """.trimIndent()
        )
    }
}
