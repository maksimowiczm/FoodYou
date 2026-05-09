@file:MustUseReturnValues

package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchHistory.Companion.MAX_HISTORY_SIZE
import kotlin.time.Clock

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

/** Records a search query if it's a text query. */
fun SearchHistory.recordSearchQuery(
    query: SearchQuery.NotBlank,
    clock: Clock = Clock.System,
): List<SearchHistoryEvent> = buildList {
    if (query is SearchQuery.Text && history.firstOrNull() != query)
        add(SearchQueryRecordedEvent(query, clock.now()))
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
