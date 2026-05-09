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

/**
 * Records a search query if it's a text query.
 *
 * Non-text queries (barcode, URLs) are ignored for history.
 */
fun SearchHistory.recordSearchQuery(
    query: SearchQuery.NotBlank,
    clock: Clock = Clock.System,
): List<SearchHistoryEvent> = buildList {
    when (query) {
        is SearchQuery.Barcode,
        is SearchQuery.OpenFoodFactsUrl,
        is SearchQuery.FoodDataCentralUrl -> return@buildList

        is SearchQuery.Text -> Unit
    }

    if (history.firstOrNull() != query) add(SearchQueryRecordedEvent(query, clock.now()))
}

/** Applies a [SearchHistoryEvent] and returns a new immutable [SearchHistory] instance. */
fun SearchHistory.apply(event: SearchHistoryEvent): SearchHistory =
    when (event) {
        is SearchQueryRecordedEvent -> {
            val newHistory = listOf(event.query) + history.filterNot { it == event }
            copy(history = newHistory.take(MAX_HISTORY_SIZE))
        }
    }

fun Iterable<SearchHistoryEvent>.toFoodSearchHistory(): SearchHistory =
    fold(SearchHistory()) { state, event -> state.apply(event) }
