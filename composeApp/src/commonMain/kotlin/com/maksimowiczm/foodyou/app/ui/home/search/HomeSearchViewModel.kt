package com.maksimowiczm.foodyou.app.ui.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.search.application.SearchHistoryService
import com.maksimowiczm.foodyou.search.domain.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchQueryParser
import com.maksimowiczm.foodyou.search.domain.recordSearchQuery
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class HomeSearchViewModel(
    private val searchQueryParser: SearchQueryParser,
    private val searchHistoryService: SearchHistoryService,
    appProfileManager: AppProfileManager,
) : ViewModel() {
    val searchQuery: SharedFlow<SearchQuery>
        field = MutableSharedFlow<SearchQuery>(replay = 1).apply { tryEmit(SearchQuery.Blank) }

    fun search(query: String?) {
        viewModelScope.launch { searchQuery.emit(searchQueryParser.parse(query)) }
    }

    val searchHistory: StateFlow<List<String>?> =
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
                initialValue = null,
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
}
