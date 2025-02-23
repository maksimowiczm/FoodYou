package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.feature.addfood.database.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.addfood.database.MealEntity
import com.maksimowiczm.foodyou.feature.addfood.database.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementTypeConverter
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.ProductDatabase
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.ProductEntity
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.ProductSourceConverter
import com.maksimowiczm.foodyou.feature.openfoodfacts.database.WeightUnitConverter

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
    ProductDatabase,
    AddFoodDatabase,
    OpenFoodFactsDatabase {
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
