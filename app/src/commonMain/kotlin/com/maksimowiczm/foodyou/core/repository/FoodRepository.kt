package com.maksimowiczm.foodyou.core.repository

import com.maksimowiczm.foodyou.core.data.source.ProductLocalDataSource
import com.maksimowiczm.foodyou.core.data.source.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.mapper.RecipeMapper
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FoodRepository {
    fun observeFood(id: FoodId): Flow<Food?>

    suspend fun deleteFood(id: FoodId)
}

internal class FoodRepositoryImpl(
    private val productDao: ProductLocalDataSource,
    private val recipeDao: RecipeLocalDataSource
) : FoodRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeFood(id: FoodId): Flow<Food?> = when (id) {
        is FoodId.Product ->
            productDao
                .observeProduct(id.id)
                .map { with(ProductMapper) { it?.toModel() } }

        is FoodId.Recipe ->
            recipeDao
                .observeRecipe(id.id)
                .map { with(RecipeMapper) { it?.toModel() } }
    }

    override suspend fun deleteFood(id: FoodId) {
        when (id) {
            is FoodId.Product -> productDao.deleteProduct(id.id)
            is FoodId.Recipe -> recipeDao.deleteRecipe(id.id)
        }
    }
}
