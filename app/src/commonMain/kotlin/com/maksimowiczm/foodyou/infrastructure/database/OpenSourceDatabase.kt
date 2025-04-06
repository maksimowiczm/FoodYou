package com.maksimowiczm.foodyou.infrastructure.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.meal.InitializeMealsCallback
import com.maksimowiczm.foodyou.feature.diary.database.meal.MealEntity
import com.maksimowiczm.foodyou.feature.diary.database.openfoodfacts.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.feature.diary.database.product.ProductEntity
import com.maksimowiczm.foodyou.feature.diary.database.product.ProductSourceConverter

@Database(
    entities = [
        MealEntity::class,
        ProductEntity::class,
        OpenFoodFactsPagingKey::class
    ],
    version = OpenSourceDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(
    ProductSourceConverter::class
)
abstract class OpenSourceDatabase :
    RoomDatabase(),
    DiaryDatabase {
    companion object {
        const val VERSION = 3

//        private val migrations: List<Migration> = listOf(
//            MIGRATION_1_2
//        )

        fun Builder<OpenSourceDatabase>.buildDatabase(
            initializeMealsCallback: InitializeMealsCallback
        ): OpenSourceDatabase {
//            migrations.forEach(::addMigrations)
            addCallback(initializeMealsCallback)
            return build()
        }
    }
}
