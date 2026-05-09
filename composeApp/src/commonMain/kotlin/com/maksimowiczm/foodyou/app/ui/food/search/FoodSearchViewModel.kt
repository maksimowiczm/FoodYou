package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.search.SearchQueryParser
import com.maksimowiczm.foodyou.search.application.SearchHistoryService
import com.maksimowiczm.foodyou.search.domain.recordSearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

internal class FoodSearchViewModel(
    initialQuery: String?,
    private val searchQueryParser: SearchQueryParser,
    private val searchHistoryService: SearchHistoryService,
    appProfileManager: AppProfileManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val savedSearchQuery = savedStateHandle.getMutableStateFlow("query", initialQuery)

    val searchQuery: Flow<SearchQuery> = savedSearchQuery.map(searchQueryParser::parse)

    val searchHistory =
        appProfileManager
            .observeAppProfileId()
            .filterNotNull()
            .flatMapLatest { profileId ->
                searchHistoryService.observe(profileId).map { history ->
                    history.history.map { it.query }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = emptyList(),
            )

    // Side effect to save search query to history
    init {
        searchQuery
            .filterIsInstance<SearchQuery.NotBlank>()
            .onEach { query ->
                val profileId = appProfileManager.observeAppProfileId().filterNotNull().first()
                searchHistoryService.transact(profileId) { it.recordSearchQuery(query) }
            }
            .launchIn(viewModelScope)
    }

    fun search(query: String?) {
        savedSearchQuery.value = query
    }
}
