package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.diary.data.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.cases.MeasuredIngredient
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.cases.ObserveIngredientsCase
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.cases.ObserveProductsCase
import com.maksimowiczm.foodyou.feature.diary.ui.recipe.model.Ingredient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateRecipeViewModel(
    private val observeIngredientsCase: ObserveIngredientsCase,
    private val observeProductsCase: ObserveProductsCase,
    searchRepository: SearchRepository
) : ViewModel() {

    val recentQueries = searchRepository.observeProductQueries(20).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    private val _ingredients = MutableStateFlow(emptyList<MeasuredIngredient>())

    fun onAddIngredient(ingredient: MeasuredIngredient) {
        _ingredients.value = _ingredients.value + ingredient
    }

    fun onEditIngredient(index: Int, measurement: WeightMeasurement) {
        _ingredients.value = _ingredients.value.mapIndexed { i, ingredient ->
            if (i == index) {
                ingredient.copy(weightMeasurement = measurement)
            } else {
                ingredient
            }
        }
    }

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

    private val searchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = combine(
        searchQuery,
        _ingredients
    ) { query, ingredients ->
        query to ingredients
    }.flatMapLatest { (query, ingredients) ->
        observeProductsCase(query, ingredients)
    }.cachedIn(viewModelScope)

    fun onSearch(query: String?) {
        viewModelScope.launch {
            searchQuery.emit(query)
        }
    }
}
