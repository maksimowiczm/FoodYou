package com.maksimowiczm.foodyou.userfood.infrastructure

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnitConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.UuidConverter
import com.maksimowiczm.foodyou.userfood.infrastructure.product.ProductDao
import com.maksimowiczm.foodyou.userfood.infrastructure.product.ProductEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.product.QuantityTypeConverter
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.RecipeDao
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.RecipeEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.RecipeQuantityTypeConverter
import com.maksimowiczm.foodyou.userfood.infrastructure.search.ProductFts
import com.maksimowiczm.foodyou.userfood.infrastructure.search.RecipeFts
import com.maksimowiczm.foodyou.userfood.infrastructure.search.SearchDao

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
    RecipeQuantityTypeConverter::class,
    UuidConverter::class,
)
@ConstructedBy(UserFoodDatabaseConstructor::class)
internal abstract class UserFoodDatabase : RoomDatabase() {
    abstract val productDao: ProductDao
    abstract val recipeDao: RecipeDao
    abstract val searchDao: SearchDao

    companion object {
        const val VERSION = 1

        fun Builder<UserFoodDatabase>.buildDatabase(): UserFoodDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object UserFoodDatabaseConstructor : RoomDatabaseConstructor<UserFoodDatabase> {
    override fun initialize(): UserFoodDatabase
}
