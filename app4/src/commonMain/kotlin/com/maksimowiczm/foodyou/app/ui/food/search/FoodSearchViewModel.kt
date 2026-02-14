package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQueryParser
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class FoodSearchViewModel(
    initialQuery: String?,
    private val searchQueryParser: SearchQueryParser,
    private val searchHistoryRepository: FoodSearchHistoryRepository,
    private val clock: Clock,
    appAccountManager: AppAccountManager,
) : ViewModel() {
    val filter: StateFlow<FoodFilter>
        field = MutableStateFlow(FoodFilter())

    // Use shared flow to allow emitting same value multiple times
    val searchQuery: Flow<SearchQuery>
        field =
            MutableSharedFlow<SearchQuery>(replay = 1).apply {
                runBlocking { emit(searchQueryParser.parse(initialQuery)) }
            }

    fun changeSource(source: FoodFilter.Source) {
        filter.update { it.copy(source = source) }
    }

    fun search(query: String?) {
        viewModelScope.launch {
            val parsedQuery = searchQueryParser.parse(query)
            searchQuery.emit(parsedQuery)
        }
    }

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
}
