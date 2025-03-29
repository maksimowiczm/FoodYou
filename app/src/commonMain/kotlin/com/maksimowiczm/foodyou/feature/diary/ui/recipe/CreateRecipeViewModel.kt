package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.cases.ObserveIngredientsCase
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.model.Ingredient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CreateRecipeViewModel(private val observeIngredientsCase: ObserveIngredientsCase) :
    ViewModel() {
    private val _ingredients = MutableStateFlow(
        listOf(
            InternalIngredient(
                productId = FoodId.Product(2L),
                weightMeasurement = WeightMeasurement.WeightUnit(100f)
            ),
            InternalIngredient(
                productId = FoodId.Product(3L),
                weightMeasurement = WeightMeasurement.WeightUnit(200f)
            ),
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val ingredients = _ingredients.flatMapLatest { ingredients ->
        observeIngredientsCase(ingredients.map { it.productId }).map { products ->
            ingredients.zip(products) { ingredient, product ->
                Ingredient(
                    product = product,
                    weightMeasurement = ingredient.weightMeasurement
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}

private data class InternalIngredient(
    val productId: FoodId.Product,
    val weightMeasurement: WeightMeasurement
)
