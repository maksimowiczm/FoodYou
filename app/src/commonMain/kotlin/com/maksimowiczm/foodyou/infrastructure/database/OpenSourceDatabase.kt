package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.AutoMigration
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
import com.maksimowiczm.foodyou.feature.diary.database.entity.RecipeEntity
import com.maksimowiczm.foodyou.feature.diary.database.entity.RecipeIngredientEntity
import com.maksimowiczm.foodyou.feature.diary.database.measurement.RecipeMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.database.measurement.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.diary.database.view.RecipeNutritionView

@Database(
    entities = [
        OpenFoodFactsPagingKey::class,
        ProductEntity::class,
        WeightMeasurementEntity::class,
        ProductQueryEntity::class,
        MealEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        RecipeMeasurementEntity::class
    ],
    views = [
        RecipeNutritionView::class
    ],
    version = OpenSourceDatabase.VERSION,
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ],
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
