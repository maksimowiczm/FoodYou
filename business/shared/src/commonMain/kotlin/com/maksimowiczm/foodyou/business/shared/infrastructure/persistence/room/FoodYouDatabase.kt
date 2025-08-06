package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventTypeConverter
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodSearchDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodSourceTypeConverter
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.ProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeAllIngredientsView
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.SearchEntry
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryProductEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryRecipeIngredientEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.InitializeMealsCallback
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MealDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MealEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.MeasurementEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.openfoodfacts.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.MeasurementTypeConverter
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.usda.USDAPagingKeyDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.usda.USDAPagingKeyEntity

@Database(
    entities =
        [
            ProductEntity::class,
            RecipeEntity::class,
            RecipeIngredientEntity::class,
            OpenFoodFactsPagingKeyEntity::class,
            USDAPagingKeyEntity::class,
            FoodEventEntity::class,
            SearchEntry::class,
            MealEntity::class,
            MeasurementEntity::class,
            DiaryProductEntity::class,
            DiaryRecipeEntity::class,
            DiaryRecipeIngredientEntity::class,
        ],
    views = [RecipeAllIngredientsView::class],
    version = FoodYouDatabase.VERSION,
    exportSchema = false, // TODO
)
@TypeConverters(
    FoodSourceTypeConverter::class,
    MeasurementTypeConverter::class,
    FoodEventTypeConverter::class,
)
abstract class FoodYouDatabase : RoomDatabase() {
    abstract val productDao: ProductDao
    abstract val recipeDao: RecipeDao
    abstract val foodSearchDao: FoodSearchDao
    abstract val openFoodFactsDao: OpenFoodFactsDao
    abstract val usdaPagingKeyDao: USDAPagingKeyDao
    abstract val foodEventDao: FoodEventDao
    abstract val measurementDao: MeasurementDao
    abstract val mealDao: MealDao

    companion object {
        const val VERSION = 25

        private val migrations: List<Migration> =
            listOf(
                // TODO
            )

        fun Builder<FoodYouDatabase>.buildDatabase(
            mealsCallback: InitializeMealsCallback
        ): FoodYouDatabase {
            addMigrations(*migrations.toTypedArray())
            addCallback(mealsCallback)
            return build()
        }
    }
}
