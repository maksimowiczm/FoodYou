package com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class OpenFoodFactsSearchViewModel(
    private val repository: OpenFoodFactsRepository,
    searchPreferencesRepository: FoodSearchPreferencesRepository,
) : ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    private val searchParameters =
        searchQuery.map { query ->
            OpenFoodFactsSearchParameters(
                query = query,
                orderBy = OpenFoodFactsSearchParameters.OrderBy.Relevance,
            )
        }

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

    private val enabled = searchPreferencesRepository.observe().map { it.allowOpenFoodFacts }

    val shouldShowFilter =
        combine(count, enabled) { count, enabled -> enabled || (count != null && count > 0) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = false,
            )

    fun search(query: SearchQuery) {
        viewModelScope.launch { searchQuery.emit(query) }
    }

    private companion object {
        private const val PAGE_SIZE = 200
    }
}
