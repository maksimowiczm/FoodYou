package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.feature.addfood.database.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementTypeConverter
import com.maksimowiczm.foodyou.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.feature.product.database.ProductEntity
import com.maksimowiczm.foodyou.feature.product.database.ProductSourceConverter
import com.maksimowiczm.foodyou.feature.product.database.WeightUnitConverter

@Database(
    entities = [
        ProductEntity::class,
        WeightMeasurementEntity::class,
        ProductQueryEntity::class
    ],
    version = FoodYouDatabase.VERSION
)
@TypeConverters(
    WeightUnitConverter::class,
    ProductSourceConverter::class,
    WeightMeasurementTypeConverter::class
)
abstract class FoodYouDatabase :
    ProductDatabase,
    AddFoodDatabase,
    RoomDatabase() {

    companion object {
        const val VERSION = 1

        val migrations: List<Migration> = emptyList()

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase {
            migrations.forEach(::addMigrations)
            return build()
        }
    }
}
