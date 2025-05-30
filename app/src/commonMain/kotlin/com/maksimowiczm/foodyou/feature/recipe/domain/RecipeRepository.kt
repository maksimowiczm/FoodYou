package com.maksimowiczm.foodyou.feature.recipe.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.core.data.database.food.MeasurementSuggestionView
import com.maksimowiczm.foodyou.core.data.database.recipe.RecipeDao
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.ext.mapValues
import kotlinx.coroutines.flow.Flow

internal class RecipeRepository(
    // TODO replace with interface
    private val recipeLocalDataSource: RecipeDao,
    private val measurementMapper: MeasurementMapper = MeasurementMapper
) {
    fun queryIngredients(query: String?): Flow<PagingData<IngredientSearchItem>> {
        val barcode = query?.takeIf { it.isNotBlank() }?.takeIf { it.all { it.isDigit() } }
        val realQuery = query?.takeIf { barcode == null }

        return Pager(
            config = PagingConfig(
                pageSize = 30
            )
        ) {
            recipeLocalDataSource.queryIngredients(realQuery, barcode)
        }.flow.mapValues {
            with(measurementMapper) { toSearchFoodItem(it) }
        }
    }
}

private fun MeasurementMapper.toSearchFoodItem(entity: MeasurementSuggestionView) = with(entity) {
    val foodId = when {
        productId != null && recipeId == null -> FoodId.Product(productId)
        recipeId != null && productId == null -> FoodId.Recipe(recipeId)
        else -> error("Data inconsistency: productId and recipeId are null")
    }

    IngredientSearchItem(
        foodId = foodId,
        headline = brand?.let { "$name ($brand)" } ?: name,
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        fats = fats,
        packageWeight = packageWeight?.let { PortionWeight.Package(it) },
        servingWeight = servingWeight?.let { PortionWeight.Serving(it) },
        measurement = toMeasurement(),
        uniqueId = foodId.toString()
    )
}
