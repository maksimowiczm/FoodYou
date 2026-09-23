@file:MustUseReturnValues

package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchHistory.Companion.MAX_HISTORY_SIZE

/**
 * [SearchHistory] is a projection of [SearchHistoryEvent]s.
 *
 * It maintains a list of recently searched text queries, limited to [MAX_HISTORY_SIZE].
 */
data class SearchHistory(val history: List<SearchQuery.NotBlank> = emptyList()) {
    companion object {
        const val MAX_HISTORY_SIZE = 20
    }
}

fun SearchHistory.decide(command: SearchHistoryCommand): List<SearchHistoryEvent> =
    when (command) {
        is SearchHistoryCommand.RecordSearchQuery ->
            buildList {
                if (history.firstOrNull() != command.query) {
                    add(SearchQueryRecordedEvent(command.query))
                }
            }
    }

/** Applies a [SearchHistoryEvent] and returns a new immutable [SearchHistory] instance. */
fun SearchHistory.apply(event: SearchHistoryEvent): SearchHistory =
    when (event) {
        is SearchQueryRecordedEvent -> {
            val newHistory = listOf(event.query) + history.filterNot { it == event.query }
            copy(history = newHistory.take(MAX_HISTORY_SIZE))
        }
    }

fun Iterable<SearchHistoryEvent>.toFoodSearchHistory(): SearchHistory =
    fold(SearchHistory()) { state, event -> state.apply(event) }

val searchHistoryDecider =
    Decider<SearchHistoryCommand, SearchHistoryEvent, SearchHistory>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = SearchHistory(),
    )
