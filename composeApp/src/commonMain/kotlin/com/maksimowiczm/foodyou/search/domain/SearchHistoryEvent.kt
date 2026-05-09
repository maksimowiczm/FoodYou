package com.maksimowiczm.foodyou.search.domain

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed interface SearchHistoryEvent : DomainEvent

@Serializable
data class SearchQueryRecordedEvent(
    val query: SearchQuery.NotBlank,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : SearchHistoryEvent
