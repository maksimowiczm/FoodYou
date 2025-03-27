package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.maksimowiczm.foodyou.feature.search.database.SearchDatabase
import com.maksimowiczm.foodyou.feature.search.database.entity.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductQueryEntity

@Database(
    entities = [
        ProductEntity::class,
        OpenFoodFactsPagingKeyEntity::class,
        ProductQueryEntity::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    SearchDatabase {
    companion object {
        const val VERSION = 1

        private val migrations: List<Migration> = emptyList()

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase {
            migrations.forEach(::addMigrations)
            return build()
        }
    }
}
