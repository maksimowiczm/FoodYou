package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.ext.combine
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.repository.FoodRepository
import com.maksimowiczm.foodyou.core.repository.SearchRepository
import com.maksimowiczm.foodyou.feature.measurement.ObserveMeasurableFoodUseCase
import com.maksimowiczm.foodyou.feature.recipe.data.RecipeRepository
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pro.respawn.kmmutils.inputforms.Form
import pro.respawn.kmmutils.inputforms.ValidationStrategy
import pro.respawn.kmmutils.inputforms.default.Rules

private data class IngredientInternal(val measurement: Measurement, val productId: FoodId.Product)

internal class RecipeViewModel(
    private val foodRepository: FoodRepository,
    searchRepository: SearchRepository,
    private val recipeRepository: RecipeRepository,
    private val observeMeasurableFoodUseCase: ObserveMeasurableFoodUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<RecipeState>(RecipeState())
    val state = _state.asStateFlow()

    private val _ingredients = MutableStateFlow<List<IngredientInternal>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val ingredients = _ingredients.flatMapLatest {
        it.map {
            foodRepository
                .observeFood(it.productId)
                .filterNotNull()
                .map { product ->
                    product as Product

                    Ingredient(
                        product = product,
                        measurement = it.measurement
                    )
                }
        }.combine { it.toList() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    val recentQueries = searchRepository.observeRecentQueries(20).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    private val _searchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }
    val searchQuery = _searchQuery.asSharedFlow()

    fun onSearch(query: String?) {
        _searchQuery.tryEmit(query)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = _searchQuery.flatMapLatest { query ->
        recipeRepository.queryProducts(query)
    }.cachedIn(viewModelScope)

    fun observeMeasurableFood(productId: Long) =
        observeMeasurableFoodUseCase(FoodId.Product(productId))

    fun onAddIngredient(ingredient: Ingredient) {
        _ingredients.update {
            it + IngredientInternal(
                measurement = ingredient.measurement,
                productId = ingredient.product.id
            )
        }
    }

    fun onUpdateIngredient(index: Int, ingredient: Ingredient) {
        _ingredients.update {
            it.mapIndexed { i, item ->
                if (i == index) {
                    IngredientInternal(
                        measurement = ingredient.measurement,
                        productId = ingredient.product.id
                    )
                } else {
                    item
                }
            }
        }
    }

    fun onRemoveIngredient(ingredient: Ingredient) {
        _ingredients.update {
            it.filterNot { item ->
                item.productId == ingredient.product.id
            }
        }
    }

    private val nameForm = Form(
        strategy = ValidationStrategy.LazyEval,
        Rules.NonEmpty
    )

    fun onNameChange(name: String) {
        _state.update {
            it.copy(
                name = nameForm(name)
            )
        }
    }

    private val servingsForm = Form(
        strategy = ValidationStrategy.LazyEval,
        Rules.DigitsOnly
    )

    fun onServingsChange(servings: String) {
        _state.update {
            it.copy(
                servings = servingsForm(servings)
            )
        }
    }

    fun onProductDelete(productId: Long) {
        viewModelScope.launch {
            foodRepository.deleteFood(FoodId.Product(productId))
        }
    }

    fun onCreate() {
    }
}
