package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class SearchHistoryTest {
    @Test
    fun recordSearchQuery_storesOnlyTextQueries() {
        val history = SearchHistory()

        val barcode = SearchQuery.Barcode("1234567890123")
        val events1 = history.recordSearchQuery(barcode)
        assertEquals(0, events1.size)

        val other = TestNotBlankQuery("https://example.com/product/123")
        val events2 = history.recordSearchQuery(other)
        assertEquals(0, events2.size)

        val text = SearchQuery.Text("apple")
        val events3 = history.recordSearchQuery(text)
        assertEquals(1, events3.size)
        assertEquals(text, (events3[0] as SearchQueryRecordedEvent).query)
    }

    @Test
    fun recordSearchQuery_doesNotStoreDuplicateQueries() {
        val query = SearchQuery.Text("apple")
        val history = SearchHistory(history = listOf(query))

        val events = history.recordSearchQuery(query)

        assertTrue(events.isEmpty())
    }

    @Test
    fun apply_updatesHistoryAndRemovesDuplicates() {
        val history = SearchHistory(history = listOf(SearchQuery.Text("banana")))
        val query = SearchQuery.Text("apple")
        val event = SearchQueryRecordedEvent(query, Instant.DISTANT_PAST)

        val updated = history.apply(event)

        assertEquals(2, updated.history.size)
        assertEquals(query, updated.history[0])
        assertEquals(SearchQuery.Text("banana"), updated.history[1])

        val duplicateEvent =
            SearchQueryRecordedEvent(SearchQuery.Text("banana"), Instant.DISTANT_PAST)
        val updated2 = updated.apply(duplicateEvent)

        assertEquals(2, updated2.history.size)
        assertEquals(SearchQuery.Text("banana"), updated2.history[0])
        assertEquals(query, updated2.history[1])
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

    private data class TestNotBlankQuery(override val query: String) : SearchQuery.NotBlank
}
