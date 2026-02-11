package com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FoodDataCentralSearchViewModel(
    private val repository: FoodDataCentralRepository,
    searchPreferencesRepository: FoodSearchPreferencesRepository,
) : ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    private val searchParameters =
        searchQuery.map { query ->
            FoodDataCentralSearchParameters(
                query = query,
                orderBy = FoodDataCentralSearchParameters.OrderBy.NameAscending,
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

    private val enabled = searchPreferencesRepository.observe().map { it.allowFoodDataCentralUSDA }

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
