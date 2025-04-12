package com.maksimowiczm.foodyou.feature.recipe.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.database.recipe.IngredientVirtualEntity
import com.maksimowiczm.foodyou.core.database.recipe.RecipeDao
import com.maksimowiczm.foodyou.core.mapper.ProductMapper
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.repository.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RecipeRepository(
    database: FoodYouDatabase,
    private val remoteMediatorFactory: ProductRemoteMediatorFactory
) {
    private val recipeDao: RecipeDao = database.recipeDao

    @OptIn(ExperimentalPagingApi::class)
    fun queryProducts(query: String?): Flow<PagingData<Ingredient>> = Pager(
        config = PagingConfig(
            pageSize = 30
        ),
        remoteMediator = remoteMediatorFactory.createWithQuery(query)
    ) {
        recipeDao.observeProductsByText(query)
    }.flow.map {
        it.map { it.toIngredient() }
    }

    fun createRecipe(name: String, servings: Int, ingredients: List<Ingredient>): Long {
        TODO()
    }
}

private fun IngredientVirtualEntity.toIngredient(): Ingredient {
    val product = with(ProductMapper) { productEntity.toModel() }

    return Ingredient(
        product = product,
        measurement = toMeasurement()
    )
}

private fun IngredientVirtualEntity.toMeasurement(): Measurement = when (measurement) {
    MeasurementEntity.Gram -> Measurement.Gram(quantity)
    MeasurementEntity.Package -> Measurement.Package(quantity)
    MeasurementEntity.Serving -> Measurement.Serving(quantity)
}
