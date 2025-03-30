package com.maksimowiczm.foodyou.feature.diary.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.ext.combine
import com.maksimowiczm.foodyou.ext.sumOf
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.Recipe
import com.maksimowiczm.foodyou.feature.diary.data.model.RecipeIngredient
import com.maksimowiczm.foodyou.feature.diary.data.model.sum
import com.maksimowiczm.foodyou.feature.diary.data.model.toDomain
import com.maksimowiczm.foodyou.feature.diary.data.model.toWeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.database.dao.ProductDao
import com.maksimowiczm.foodyou.feature.diary.database.recipe.RecipeDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class RecipeRepositoryImpl(database: DiaryDatabase) : RecipeRepository {

    private val recipeDao: RecipeDao = database.recipeDao
    private val productDao: ProductDao = database.productDao()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeRecipeById(id: Long): Flow<Recipe?> {
        return recipeDao.observeRecipeById(id).flatMapLatest { recipeEntity ->
            if (recipeEntity == null) {
                return@flatMapLatest flowOf(null)
            }

            recipeEntity.ingredients.map { ingredient ->
                productDao
                    .observeProductById(ingredient.productId)
                    .filterNotNull()
                    .map {
                        val product = it.toDomain()
                        val measurement =
                            ingredient.measurement.toWeightMeasurement(ingredient.quantity)

                        RecipeIngredient(
                            product = product,
                            weightMeasurement = measurement
                        )
                    }
            }.combine { products ->
                val packageWeight = products.sumOf { it.weight }
                val servingWeight = packageWeight / recipeEntity.recipe.servings
                val nutrients = products.map { it.product.nutrients }.sum()

                Recipe(
                    id = FoodId.Recipe(recipeEntity.recipe.id),
                    name = recipeEntity.recipe.name,
                    nutrients = nutrients,
                    packageWeight = packageWeight,
                    servingWeight = servingWeight,
                    ingredients = products.toList()
                )
            }
        }
    }

    override suspend fun deleteRecipe(id: Long): Result<Unit, RecipeDeletionError> {
        val entity = recipeDao.getRecipeById(id)

        if (entity == null) {
            return Err(RecipeDeletionError.PRODUCT_NOT_FOUND)
        }

        recipeDao.deleteRecipe(entity)
        return Ok(Unit)
    }
}
