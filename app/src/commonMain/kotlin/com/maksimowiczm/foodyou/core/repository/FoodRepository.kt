package com.maksimowiczm.foodyou.core.repository

import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.database.product.ProductDao
import com.maksimowiczm.foodyou.core.database.recipe.RecipeDao
import com.maksimowiczm.foodyou.core.ext.combine
import com.maksimowiczm.foodyou.core.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import com.maksimowiczm.foodyou.core.model.RecipeIngredient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface FoodRepository {
    fun observeFood(id: FoodId): Flow<Food?>

    suspend fun deleteFood(id: FoodId)
}

internal class FoodRepositoryImpl(database: FoodYouDatabase) : FoodRepository {
    private val productDao: ProductDao = database.productDao
    private val recipeDao: RecipeDao = database.recipeDao

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeFood(id: FoodId): Flow<Food?> = when (id) {
        is FoodId.Product ->
            productDao
                .observeProduct(id.id)
                .map { with(ProductMapper) { it?.toModel() } }

        is FoodId.Recipe -> recipeDao.observeRecipe(id.id).flatMapLatest {
            if (it == null) {
                return@flatMapLatest flowOf(null)
            }

            val (recipeEntity, ingredients) = it

            val products = ingredients.map { ingredient ->
                productDao
                    .observeProduct(ingredient.productId)
                    .filterNotNull()
                    .map {
                        val product = with(ProductMapper) { it.toModel() }
                        val quantity = ingredient.quantity
                        val measurement = when (ingredient.measurement) {
                            MeasurementEntity.Gram -> Measurement.Gram(quantity)
                            MeasurementEntity.Package -> Measurement.Package(quantity)
                            MeasurementEntity.Serving -> Measurement.Serving(quantity)
                        }

                        RecipeIngredient(
                            product = product,
                            measurement = measurement
                        )
                    }
            }.combine { it.toList() }

            products.map { products ->
                Recipe(
                    id = FoodId.Recipe(recipeEntity.id),
                    name = recipeEntity.name,
                    servings = recipeEntity.servings,
                    ingredients = products
                )
            }
        }
    }

    override suspend fun deleteFood(id: FoodId) {
        when (id) {
            is FoodId.Product -> productDao.deleteProduct(id.id)
            is FoodId.Recipe -> recipeDao.deleteRecipe(id.id)
        }
    }
}
