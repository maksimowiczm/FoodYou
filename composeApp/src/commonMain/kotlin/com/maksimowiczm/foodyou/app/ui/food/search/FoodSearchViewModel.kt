package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQueryParser
import kotlin.time.Clock
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
    private val searchHistoryRepository: FoodSearchHistoryRepository,
    private val clock: Clock,
    appAccountManager: AppAccountManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val savedSearchQuery = savedStateHandle.getMutableStateFlow("query", initialQuery)

    val searchQuery: Flow<SearchQuery> = savedSearchQuery.map(searchQueryParser::parse)

    val searchHistory =
        appAccountManager
            .observeAppProfileId()
            .filterNotNull()
            .flatMapLatest { profileId ->
                searchHistoryRepository.observe(profileId).map { history ->
                    history.history.map { it.query.query }
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
            .onEach {
                val profileId = appAccountManager.observeAppProfileId().filterNotNull().first()
                val history = searchHistoryRepository.observe(profileId).first()
                history.recordSearchQuery(it, clock)
                searchHistoryRepository.save(history)
            }
            .launchIn(viewModelScope)
    }

    fun search(query: String?) {
        savedSearchQuery.value = query
    }
}
