package com.maksimowiczm.foodyou.core.domain.repository

import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun observeFood(id: FoodId): Flow<Food?>
    suspend fun deleteFood(id: FoodId)
}

// internal class FoodRepositoryImpl(
//    private val productLocalDataSource: ProductLocalDataSource,
//    private val recipeLocalDataSource: RecipeLocalDataSource,
//    private val recipeRepository: RecipeRepository
// ) : FoodRepository {
//    override fun observeFood(id: FoodId): Flow<Food?> = when (id) {
//        is FoodId.Product ->
//            productLocalDataSource
//                .observeProduct(id.id)
//                .map { with(ProductMapper) { it?.toModel() } }
//
//        is FoodId.Recipe -> recipeRepository.observeRecipe(id)
//    }
//
//    override suspend fun deleteFood(id: FoodId) {
//        when (id) {
//            is FoodId.Product -> productLocalDataSource.deleteProduct(id.id)
//            is FoodId.Recipe -> recipeLocalDataSource.deleteRecipe(id.id)
//        }
//    }
// }
