package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.maksimowiczm.foodyou.database.callback.InitializeMealsCallback
import com.maksimowiczm.foodyou.database.converter.ProductSourceConverter
import com.maksimowiczm.foodyou.database.converter.WeightMeasurementTypeConverter
import com.maksimowiczm.foodyou.database.converter.WeightUnitConverter
import com.maksimowiczm.foodyou.database.dao.AddFoodDao
import com.maksimowiczm.foodyou.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.database.dao.ProductDao
import com.maksimowiczm.foodyou.database.entity.MealEntity
import com.maksimowiczm.foodyou.database.entity.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.database.entity.ProductEntity
import com.maksimowiczm.foodyou.database.entity.ProductQueryEntity
import com.maksimowiczm.foodyou.database.entity.WeightMeasurementEntity

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
abstract class OpenSourceDatabase : RoomDatabase() {
    abstract fun addFoodDao(): AddFoodDao
    abstract fun productDao(): ProductDao
    abstract fun openFoodFactsDao(): OpenFoodFactsDao

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
    private val query = """
        ALTER TABLE MealEntity
        ADD COLUMN lexoRank TEXT NOT NULL DEFAULT 'a'
    """.trimIndent()

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(query)
    }
}
