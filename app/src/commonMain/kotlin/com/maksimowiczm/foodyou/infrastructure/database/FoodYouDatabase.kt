package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.callback.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import com.maksimowiczm.foodyou.feature.search.database.SearchDatabase
import com.maksimowiczm.foodyou.feature.search.database.entity.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductQueryEntity

@Database(
    entities = [
        ProductEntity::class,
        OpenFoodFactsPagingKeyEntity::class,
        ProductQueryEntity::class,
        MealEntity::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    SearchDatabase,
    DiaryDatabase {
    companion object {
        const val VERSION = 1

        private val migrations: List<Migration> = emptyList()

        fun Builder<FoodYouDatabase>.buildDatabase(
            initializeMealsCallback: InitializeMealsCallback
        ): FoodYouDatabase {
            migrations.forEach(::addMigrations)
            addCallback(initializeMealsCallback)
            return build()
        }
    }
}
