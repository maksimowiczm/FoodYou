package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
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
        const val VERSION = 1

        private val migrations: List<Migration> = emptyList()

        fun Builder<OpenSourceDatabase>.buildDatabase(
            mealsCallback: InitializeMealsCallback
        ): OpenSourceDatabase {
            migrations.forEach(::addMigrations)
            addCallback(mealsCallback)
            return build()
        }
    }
}
