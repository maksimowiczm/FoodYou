package com.maksimowiczm.foodyou.feature.recipe.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.recipe.domain.IngredientSearchItem
import com.maksimowiczm.foodyou.feature.recipe.domain.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

internal class IngredientsSearchViewModel(private val recipeRepository: RecipeRepository) :
    ViewModel() {
    private val searchQuery = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages: Flow<PagingData<IngredientSearchItem>> = searchQuery
        .flatMapLatest { query -> recipeRepository.queryIngredients(query) }
        .cachedIn(viewModelScope)

    fun onSearch(query: String?) {
        searchQuery.value = query
    }
}
