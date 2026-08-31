package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.search.SearchQueryParser
import com.maksimowiczm.foodyou.search.application.SearchHistoryService
import com.maksimowiczm.foodyou.search.domain.recordSearchQuery
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
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
import org.koin.core.component.KoinScopeComponent
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

class SearchViewModel(
    initialQuery: String?,
    val avoidCircularDependencyWith: UserRecipeId?,
    private val searchQueryParser: SearchQueryParser,
    private val searchHistoryService: SearchHistoryService,
    savedStateHandle: SavedStateHandle,
    appProfileManager: AppProfileManager,
) : ViewModel(), KoinScopeComponent {
    override val scope: Scope = getKoin().createScope(this)

    inline fun <reified T : SearchExtension> extension(): T = scope.get { parametersOf(this) }

    override fun onCleared() {
        super.onCleared()
        scope.close()
    }

    private val _searchQuery = savedStateHandle.getMutableStateFlow(SEARCH_QUERY_KEY, initialQuery)

    val searchQuery =
        _searchQuery
            .map(searchQueryParser::parse)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = searchQueryParser.parse(_searchQuery.value),
            )

    fun search(query: String?) {
        _searchQuery.value = query
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

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }
}
