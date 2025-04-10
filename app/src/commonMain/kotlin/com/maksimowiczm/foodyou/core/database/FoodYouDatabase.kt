package com.maksimowiczm.foodyou.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.core.database.callback.InitializeMealsCallback
import com.maksimowiczm.foodyou.core.database.converter.MeasurementTypeConverter
import com.maksimowiczm.foodyou.core.database.converter.ProductSourceConverter
import com.maksimowiczm.foodyou.core.database.dao.MealDao
import com.maksimowiczm.foodyou.core.database.dao.MeasurementDao
import com.maksimowiczm.foodyou.core.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.core.database.dao.ProductDao
import com.maksimowiczm.foodyou.core.database.dao.SearchDao
import com.maksimowiczm.foodyou.core.database.entity.MealEntity
import com.maksimowiczm.foodyou.core.database.entity.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.core.database.entity.ProductEntity
import com.maksimowiczm.foodyou.core.database.entity.ProductMeasurementEntity
import com.maksimowiczm.foodyou.core.database.entity.SearchQueryEntity
import com.maksimowiczm.foodyou.feature.goals.database.DiaryDayDao
import com.maksimowiczm.foodyou.feature.goals.database.DiaryDayView

@Database(
    entities = [
        MealEntity::class,
        ProductEntity::class,
        ProductMeasurementEntity::class,
        OpenFoodFactsPagingKeyEntity::class,
        SearchQueryEntity::class
    ],
    views = [
        DiaryDayView::class // This will break if the view is moved to separate gradle module
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(
    ProductSourceConverter::class,
    MeasurementTypeConverter::class
)
abstract class FoodYouDatabase : RoomDatabase() {
    abstract val mealDao: MealDao
    abstract val measurementDao: MeasurementDao
    abstract val openFoodFactsDao: OpenFoodFactsDao
    abstract val productDao: ProductDao
    abstract val searchDao: SearchDao

    // This will break if the view is moved to separate gradle module
    abstract val diaryDayDao: DiaryDayDao

    companion object {
        const val VERSION = 3

        private val migrations: List<Migration> = listOf(
            MIGRATION_1_2
        )

        fun Builder<FoodYouDatabase>.buildDatabase(
            initializeMealsCallback: InitializeMealsCallback
        ): FoodYouDatabase {
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
