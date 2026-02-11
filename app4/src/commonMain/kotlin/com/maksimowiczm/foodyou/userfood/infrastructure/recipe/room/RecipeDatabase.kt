package com.maksimowiczm.foodyou.userfood.infrastructure.recipe.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.common.infrastructure.room.MeasurementUnitConverter

@Database(
    entities = [RecipeEntity::class, RecipeIngredientEntity::class, RecipeFts::class],
    version = RecipeDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(
    FoodReferenceTypeConverter::class,
    RecipeQuantityTypeConverter::class,
    MeasurementUnitConverter::class,
)
@ConstructedBy(RecipeDatabaseConstructor::class)
internal abstract class RecipeDatabase : RoomDatabase() {
    abstract val dao: RecipeDao

    companion object {
        const val VERSION = 1

        fun Builder<RecipeDatabase>.buildDatabase(): RecipeDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object RecipeDatabaseConstructor : RoomDatabaseConstructor<RecipeDatabase> {
    override fun initialize(): RecipeDatabase
}
