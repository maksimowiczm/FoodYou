package com.maksimowiczm.foodyou.userfood.infrastructure.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnitConverter
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductDao
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductFts
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.QuantityTypeConverter
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.FoodReferenceTypeConverter
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeDao
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeFts
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeQuantityTypeConverter

@Database(
    entities =
        [
            ProductEntity::class,
            ProductFts::class,
            RecipeEntity::class,
            RecipeIngredientEntity::class,
            RecipeFts::class,
        ],
    version = UserFoodDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(
    QuantityTypeConverter::class,
    MeasurementUnitConverter::class,
    FoodReferenceTypeConverter::class,
    RecipeQuantityTypeConverter::class,
)
@ConstructedBy(UserFoodDatabaseConstructor::class)
internal abstract class UserFoodDatabase : RoomDatabase() {
    abstract val productDao: ProductDao
    abstract val recipeDao: RecipeDao

    companion object {
        const val VERSION = 1

        fun Builder<UserFoodDatabase>.buildDatabase(): UserFoodDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object UserFoodDatabaseConstructor : RoomDatabaseConstructor<UserFoodDatabase> {
    override fun initialize(): UserFoodDatabase
}
