package com.maksimowiczm.foodyou.app.ui.food.search.userfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.userfood.domain.search.UserFoodSearchParameters
import com.maksimowiczm.foodyou.userfood.domain.search.UserFoodSearchRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UserFoodSearchViewModel(private val repository: UserFoodSearchRepository) :
    ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    private val searchParameters =
        searchQuery
            .distinctUntilChanged()
            .map { query ->
                UserFoodSearchParameters(
                    query = query,
                    orderBy = UserFoodSearchParameters.OrderBy.NameAscending,
                )
            }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                replay = 1,
            )

    val pages =
        searchParameters.flatMapLatest { repository.search(it, PAGE_SIZE) }.cachedIn(viewModelScope)

    val count =
        searchParameters
            .flatMapLatest(repository::count)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun search(query: SearchQuery) {
        viewModelScope.launch { searchQuery.emit(query) }
    }

    private companion object {
        private const val PAGE_SIZE = 50
    }
}
