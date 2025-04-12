package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.model.FoodId
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pro.respawn.kmmutils.inputforms.Form
import pro.respawn.kmmutils.inputforms.ValidationStrategy
import pro.respawn.kmmutils.inputforms.default.Rules

internal class RecipeViewModel(
    private val foodRepository: FoodRepository,
    private val searchRepository: SearchRepository,
    private val recipeRepository: RecipeRepository,
    private val observeMeasurableFoodUseCase: ObserveMeasurableFoodUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<RecipeState>(RecipeState())
    val state = _state.asStateFlow()

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
        _state.update {
            it.copy(
                ingredients = it.ingredients + ingredient
            )
        }
    }

    fun onUpdateIngredient(index: Int, ingredient: Ingredient) {
        _state.update {
            it.copy(
                ingredients = it.ingredients.toMutableList().apply {
                    this[index] = ingredient
                }
            )
        }
    }

    fun onRemoveIngredient(ingredient: Ingredient) {
        _state.update {
            it.copy(
                ingredients = it.ingredients - ingredient
            )
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
