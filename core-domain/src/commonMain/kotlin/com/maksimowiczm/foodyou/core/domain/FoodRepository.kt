package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.food.ProductLocalDataSource
import com.maksimowiczm.foodyou.core.database.food.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapIfNotNull

interface FoodRepository {
    fun observeFood(id: FoodId): Flow<Food?>
    suspend fun deleteFood(id: FoodId)
}

internal class FoodRepositoryImpl(
    private val productLocalDataSource: ProductLocalDataSource,
    private val recipeLocalDataSource: RecipeLocalDataSource,
    private val recipeRepository: RecipeRepository,
    private val productMapper: ProductMapper
) : FoodRepository {
    override fun observeFood(id: FoodId): Flow<Food?> = when (id) {
        is FoodId.Product ->
            productLocalDataSource
                .observeProductById(id.id)
                .mapIfNotNull(productMapper::toModel)

        is FoodId.Recipe -> recipeRepository.observeRecipe(id)
    }

    override suspend fun deleteFood(id: FoodId) {
        when (id) {
            is FoodId.Product -> productLocalDataSource.deleteProductById(id.id)
            is FoodId.Recipe -> recipeLocalDataSource.deleteRecipeById(id.id)
        }
    }
}
