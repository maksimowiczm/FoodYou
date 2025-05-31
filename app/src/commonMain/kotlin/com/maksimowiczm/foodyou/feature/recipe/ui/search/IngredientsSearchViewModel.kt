package com.maksimowiczm.foodyou.feature.recipe.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.feature.recipe.domain.QueryIngredientsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

internal class IngredientsSearchViewModel(
    private val queryIngredientsUseCase: QueryIngredientsUseCase,
    private val recipeId: FoodId.Recipe?
) : ViewModel() {
    private val searchQuery = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ingredients = searchQuery.flatMapLatest { query ->
        queryIngredientsUseCase(
            query = query?.takeIf { it.isNotBlank() },
            excludedRecipeId = recipeId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun onSearch(query: String?) {
        searchQuery.value = query
    }
}
