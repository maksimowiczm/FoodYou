package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.account.domain.testProfileId
import com.maksimowiczm.foodyou.common.clock.testClock
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchHistory
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class FoodSearchHistoryTest {
    @Test
    fun recordSearchQuery_storesOnlyTextQueries() {
        val history = FoodSearchHistory.of(profileId = testProfileId())
        val now = Instant.fromEpochSeconds(1_000_000_000)
        val clock = testClock(now)

        history.recordSearchQuery(SearchQuery.Barcode("1234567890123"), clock)
        history.recordSearchQuery(
            SearchQuery.OpenFoodFactsUrl("https://world.openfoodfacts.org/product/1234567890123"),
            clock,
        )
        history.recordSearchQuery(
            SearchQuery.FoodDataCentralUrl(
                "https://fdc.nal.usda.gov/fdc-app.html#/food-details/1234567"
            ),
            clock,
        )
        history.recordSearchQuery(SearchQuery.Text("apple"), clock)

        assertEquals(1, history.history.size)
        assertEquals(SearchQuery.Text("apple"), history.history[0].query)
    }

    @Test
    fun recordSearchQuery_updatesTimestampForExistingQuery() {
        val history = FoodSearchHistory.of(profileId = testProfileId())
        val now1 = Instant.fromEpochSeconds(1_000_000_000)
        val clock1 = testClock(now1)
        val now2 = Instant.fromEpochSeconds(1_000_000_100)
        val clock2 = testClock(now2)

        history.recordSearchQuery(SearchQuery.Text("banana"), clock1)

        assertEquals(1, history.history.size)
        assertEquals(SearchQuery.Text("banana"), history.history[0].query)
        assertEquals(now1, history.history[0].timestamp)

        history.recordSearchQuery(SearchQuery.Text("banana"), clock2)

        assertEquals(1, history.history.size)
        assertEquals(SearchQuery.Text("banana"), history.history[0].query)
        assertEquals(now2, history.history[0].timestamp)
    }

    @Test
    fun recordSearchQuery_maintainsMaxHistorySize_oldestEntriesRemoved() {
        val history = FoodSearchHistory.of(profileId = testProfileId())
        val clock = testClock(Instant.fromEpochSeconds(1_000_000_000))
        val lastIndex =
            (FoodSearchHistory.MAX_HISTORY_SIZE + 5) % FoodSearchHistory.MAX_HISTORY_SIZE + 1

        for (i in 1..(FoodSearchHistory.MAX_HISTORY_SIZE + 5)) {
            history.recordSearchQuery(SearchQuery.Text("query $i"), clock)
        }

        assertEquals(FoodSearchHistory.MAX_HISTORY_SIZE, history.history.size)
        assertEquals("query $lastIndex", (history.history.last().query as SearchQuery.Text).query)
    }
}
