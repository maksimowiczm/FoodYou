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
        const val VERSION = 2

        private val migrations: List<Migration> = listOf(
            MIGRATION_1_2
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
