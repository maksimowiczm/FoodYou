package com.maksimowiczm.foodyou.search.application

import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.load
import com.maksimowiczm.foodyou.common.domain.observe
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.search.domain.SearchHistory
import com.maksimowiczm.foodyou.search.domain.SearchHistoryEvent
import com.maksimowiczm.foodyou.search.domain.toFoodSearchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryService(private val eventStore: EventStore, private val eventBus: EventBus) {
    private fun streamId(profileId: ProfileId) = "SearchHistory-${profileId.value}"

    fun observe(profileId: ProfileId): Flow<SearchHistory> =
        eventStore.observe<SearchHistoryEvent>(streamId(profileId)).map { events ->
            events.toFoodSearchHistory()
        }

    suspend fun transact(profileId: ProfileId, block: (SearchHistory) -> List<SearchHistoryEvent>) {
        val product = eventStore.load<SearchHistoryEvent>(streamId(profileId)).toFoodSearchHistory()
        val newEvents = block(product)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId(profileId), newEvents)
            eventBus.publish(newEvents)
        }
    }
}
