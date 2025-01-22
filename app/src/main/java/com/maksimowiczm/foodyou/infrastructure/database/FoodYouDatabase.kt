package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.room.withTransaction
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementTypeConverter
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.feature.product.database.ProductEntity
import com.maksimowiczm.foodyou.feature.product.database.ProductSourceConverter
import com.maksimowiczm.foodyou.feature.product.database.WeightUnitConverter

@Database(
    entities = [
        ProductEntity::class,
        WeightMeasurementEntity::class
    ],
    version = FoodYouDatabase.VERSION
)
@TypeConverters(
    WeightUnitConverter::class,
    ProductSourceConverter::class,
    WeightMeasurementTypeConverter::class
)
abstract class FoodYouDatabase :
    TransactionProvider,
    ProductDatabase,
    AddFoodDatabase,
    DiaryDatabase,
    RoomDatabase() {

    companion object {
        const val VERSION = 1

        val migrations: List<Migration> = emptyList()

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase {
            migrations.forEach(::addMigrations)
            return build()
        }
    }

    override suspend fun <T> withTransaction(block: suspend () -> T) = withTransactionWrapped(block)
}

private suspend fun <T> RoomDatabase.withTransactionWrapped(
    block: suspend () -> T
): T = this.withTransaction(block)
