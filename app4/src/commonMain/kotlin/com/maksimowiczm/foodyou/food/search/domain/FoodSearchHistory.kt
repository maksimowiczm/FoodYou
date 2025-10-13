package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.ProfileId
import kotlin.time.Clock

class FoodSearchHistory
private constructor(val profileId: ProfileId, history: List<SearchHistory>) {
    companion object {
        fun of(profileId: ProfileId) = FoodSearchHistory(profileId, listOf())

        private const val MAX_HISTORY_SIZE = 20
    }

    private val _history: MutableList<SearchHistory> = history.toMutableList()
    val history: List<SearchHistory>
        get() = _history.toList()

    fun recordSearchQuery(query: SearchQuery, clock: Clock) {
        _history.removeAll { it.query == query }
        _history.add(0, SearchHistory(query, clock.now()))
        if (_history.size > MAX_HISTORY_SIZE) {
            _history.removeLast()
        }
    }
}
