package com.maksimowiczm.foodyou.feature.recipe.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.core.data.model.recipe.IngredientSuggestion
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientEntity
import com.maksimowiczm.foodyou.core.data.source.RecipeLocalDataSource
import com.maksimowiczm.foodyou.core.ext.mapValues
import com.maksimowiczm.foodyou.core.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.mapper.RecipeMapper
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Recipe
import com.maksimowiczm.foodyou.core.repository.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import kotlinx.coroutines.flow.Flow

internal class RecipeRepository(
    private val recipeDao: RecipeLocalDataSource,
    private val remoteMediatorFactory: ProductRemoteMediatorFactory
) {

    @OptIn(ExperimentalPagingApi::class)
    fun queryProducts(query: String?): Flow<PagingData<Ingredient>> = Pager(
        config = PagingConfig(
            pageSize = 30
        ),
        remoteMediator = remoteMediatorFactory.createWithQuery(query)
    ) {
        recipeDao.observeIngredientSuggestions(query)
    }.flow.mapValues { it.toIngredient() }

    suspend fun getRecipeById(id: Long): Recipe? = with(RecipeMapper) {
        recipeDao.getRecipe(id)?.toModel()
    }

    suspend fun createRecipe(name: String, servings: Int, ingredients: List<Ingredient>): Long {
        val recipeEntity = RecipeEntity(
            name = name,
            servings = servings
        )
        val recipeIngredientEntities = ingredients.map { ingredient ->
            val quantity = when (ingredient.measurement) {
                is Measurement.Gram -> ingredient.measurement.value
                is Measurement.Package -> ingredient.measurement.quantity
                is Measurement.Serving -> ingredient.measurement.quantity
            }

            RecipeIngredientEntity(
                recipeId = 0L,
                productId = ingredient.product.id.id,
                measurement = with(MeasurementMapper) { ingredient.measurement.toEntity() },
                quantity = quantity
            )
        }

        return recipeDao.insertRecipeWithIngredients(
            recipeEntity = recipeEntity,
            recipeIngredientEntities = recipeIngredientEntities
        )
    }

    suspend fun updateRecipe(id: Long, name: String, servings: Int, ingredients: List<Ingredient>) {
        val recipeEntity = RecipeEntity(
            id = id,
            name = name,
            servings = servings
        )

        val recipeIngredientEntities = ingredients.map { ingredient ->
            val quantity = when (ingredient.measurement) {
                is Measurement.Gram -> ingredient.measurement.value
                is Measurement.Package -> ingredient.measurement.quantity
                is Measurement.Serving -> ingredient.measurement.quantity
            }

            RecipeIngredientEntity(
                recipeId = 0L,
                productId = ingredient.product.id.id,
                measurement = with(MeasurementMapper) { ingredient.measurement.toEntity() },
                quantity = quantity
            )
        }

        return recipeDao.updateRecipeWithIngredients(
            recipeEntity = recipeEntity,
            recipeIngredientEntities = recipeIngredientEntities
        )
    }
}

private fun IngredientSuggestion.toIngredient(): Ingredient = Ingredient(
    product = with(ProductMapper) { productEntity.toModel() },
    measurement = with(MeasurementMapper) { toMeasurement() }
)
