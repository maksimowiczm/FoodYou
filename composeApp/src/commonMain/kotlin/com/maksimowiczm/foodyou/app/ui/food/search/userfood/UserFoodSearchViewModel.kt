package com.maksimowiczm.foodyou.app.ui.food.search.userfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UserFoodSearchViewModel(
    private val repository: SearchRepository,
    foodNameSelector: FoodNameSelector,
) : ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    val pages =
        combine(searchQuery, foodNameSelector.observeLanguage()) { query, language ->
                repository.search(query, language)
            }
            .flatMapLatest { it }
            .cachedIn(viewModelScope)

    val count =
        combine(searchQuery, foodNameSelector.observeLanguage()) { query, language ->
                repository.count(query, language)
            }
            .flatMapLatest { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun search(query: SearchQuery) {
        viewModelScope.launch { searchQuery.emit(query) }
    }
}
