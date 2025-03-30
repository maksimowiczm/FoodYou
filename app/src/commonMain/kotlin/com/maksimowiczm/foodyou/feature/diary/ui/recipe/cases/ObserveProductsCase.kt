package com.maksimowiczm.foodyou.feature.diary.ui.recipe.cases

import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.feature.diary.data.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchModel
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.model.IngredientSearch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class MeasuredIngredient(
    val productId: FoodId.Product,
    val weightMeasurement: WeightMeasurement
)

class ObserveProductsCase(private val searchRepository: SearchRepository) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        query: String?,
        ingredients: List<MeasuredIngredient>
    ): Flow<PagingData<IngredientSearch>> = searchRepository.queryProducts(query).map { data ->
        data.map { searchModel ->
            searchModel.toIngredientSearch(
                selected = ingredients.any { it.productId == searchModel.foodId }
            )
        }
    }
}

private fun SearchModel.toIngredientSearch(selected: Boolean): IngredientSearch = IngredientSearch(
    name = name,
    brand = brand,
    productId = foodId as FoodId.Product,
    calories = calories.toInt(),
    proteins = proteins.toInt(),
    carbohydrates = carbohydrates.toInt(),
    fats = fats.toInt(),
    packageWeight = packageWeight,
    servingWeight = servingWeight,
    weightMeasurement = measurement,
    selected = selected
)
