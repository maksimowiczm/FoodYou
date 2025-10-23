package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.time.Clock

class FoodSearchHistory
private constructor(val profileId: ProfileId, history: List<SearchHistory>) {
    companion object {
        fun of(profileId: ProfileId) = FoodSearchHistory(profileId, listOf())

        fun of(profileId: ProfileId, history: List<SearchHistory>) =
            FoodSearchHistory(profileId, history)

        private const val MAX_HISTORY_SIZE = 20
    }

    private val _history: MutableList<SearchHistory> = history.toMutableList()
    val history: List<SearchHistory>
        get() = _history.toList()

    fun recordSearchQuery(query: SearchQuery.NotBlank, clock: Clock) {

        // Filter out non-text queries
        when (query) {
            is SearchQuery.Barcode,
            is SearchQuery.OpenFoodFactsUrl,
            is SearchQuery.FoodDataCentralUrl -> return

            is SearchQuery.Text -> Unit
        }

        _history.removeAll { it.query == query }
        _history.add(0, SearchHistory(query, clock.now()))
        if (_history.size > MAX_HISTORY_SIZE) {
            _history.removeLast()
        }
    }
}
