package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.recipe.data.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import pro.respawn.kmmutils.inputforms.Form
import pro.respawn.kmmutils.inputforms.ValidationStrategy
import pro.respawn.kmmutils.inputforms.default.Rules

internal class RecipeViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {
    private val _state = MutableStateFlow<RecipeState>(RecipeState())
    val state = _state.asStateFlow()

    private val _searchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }
    val searchQuery = _searchQuery.asSharedFlow()

    fun onSearch(query: String?) {
        _searchQuery.tryEmit(query)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = _searchQuery.flatMapLatest { query ->
        recipeRepository.queryProducts(query)
    }.cachedIn(viewModelScope)

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

    fun onCreate() {
    }
}
