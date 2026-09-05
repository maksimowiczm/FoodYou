package com.maksimowiczm.foodyou.search.application

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.asEventSink
import com.maksimowiczm.foodyou.common.event.asHandler
import com.maksimowiczm.foodyou.common.observe
import com.maksimowiczm.foodyou.common.plus
import com.maksimowiczm.foodyou.search.domain.SearchHistory
import com.maksimowiczm.foodyou.search.domain.SearchHistoryCommand
import com.maksimowiczm.foodyou.search.domain.SearchHistoryEvent
import com.maksimowiczm.foodyou.search.domain.searchHistoryDecider
import com.maksimowiczm.foodyou.search.domain.toFoodSearchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryService(
    private val eventStore: EventStore,
    eventNotifier: EventNotifier,
) {
    private val commandHandler =
        searchHistoryDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    private fun stream(profileId: ProfileId) = "SearchHistory-${profileId.value}"

    suspend fun handle(profileId: ProfileId, command: SearchHistoryCommand) {
        val _ = commandHandler(stream(profileId), command)
    }

    fun observe(profileId: ProfileId): Flow<SearchHistory> =
        eventStore.observe<SearchHistoryEvent>(stream(profileId)).map { events ->
            events.toFoodSearchHistory()
        }
}
