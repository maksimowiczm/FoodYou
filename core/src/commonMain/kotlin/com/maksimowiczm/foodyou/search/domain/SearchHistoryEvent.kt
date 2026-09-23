package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface SearchHistoryEvent : DomainEvent

@Serializable data class SearchQueryRecordedEvent(val query: SearchQuery.Text) : SearchHistoryEvent
