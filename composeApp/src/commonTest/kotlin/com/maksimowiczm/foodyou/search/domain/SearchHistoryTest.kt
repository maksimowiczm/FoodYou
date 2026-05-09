package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class SearchHistoryTest {
    @Test
    fun recordSearchQuery_storesOnlyTextQueries() {
        val history = SearchHistory()
        val now = Instant.fromEpochSeconds(1_000_000_000)
        val clock = staticClock(now)

        val events1 = history.recordSearchQuery(SearchQuery.Barcode("1234567890123"), clock)
        val events2 =
            history.recordSearchQuery(
                SearchQuery.OpenFoodFactsUrl(
                    "https://world.openfoodfacts.org/product/1234567890123"
                ),
                clock,
            )
        val events3 =
            history.recordSearchQuery(
                SearchQuery.FoodDataCentralUrl(
                    "https://fdc.nal.usda.gov/fdc-app.html#/food-details/1234567"
                ),
                clock,
            )
        val events4 = history.recordSearchQuery(SearchQuery.Text("apple"), clock)

        assertTrue(events1.isEmpty())
        assertTrue(events2.isEmpty())
        assertTrue(events3.isEmpty())
        assertEquals(1, events4.size)
        assertEquals(
            SearchQuery.Text("apple"),
            events4[0].let { it as SearchQueryRecordedEvent }.query,
        )
    }

    @Test
    fun apply_updatesHistory() {
        val history = SearchHistory()
        val now = Instant.fromEpochSeconds(1_000_000_000)
        val query = SearchQuery.Text("apple")
        val event = SearchQueryRecordedEvent(query, now)

        val updated = history.apply(event)

        assertEquals(1, updated.history.size)
        assertEquals(query, updated.history[0])
    }

    @Test
    fun apply_maintainsMaxHistorySize_oldestEntriesRemoved() {
        var history = SearchHistory()

        for (i in 1..(SearchHistory.MAX_HISTORY_SIZE + 5)) {
            val query = SearchQuery.Text("query $i")
            history =
                history.apply(SearchQueryRecordedEvent(query, Instant.fromEpochSeconds(i.toLong())))
        }

        assertEquals(SearchHistory.MAX_HISTORY_SIZE, history.history.size)
        assertEquals(
            "query ${SearchHistory.MAX_HISTORY_SIZE + 5}",
            (history.history.first() as SearchQuery.Text).query,
        )
        assertEquals("query 6", (history.history.last() as SearchQuery.Text).query)
    }
}
