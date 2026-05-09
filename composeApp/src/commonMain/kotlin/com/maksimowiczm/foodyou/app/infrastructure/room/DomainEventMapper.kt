package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.account.domain.AccountEvent
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.search.domain.SearchHistoryEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal object DomainEventMapper {
    fun toDomainEvent(entity: DomainEventEntity): DomainEvent =
        roomEventJson.decodeFromString(entity.payloadJson)

    fun toRoomStoredEventEntity(event: DomainEvent, stream: String): DomainEventEntity =
        DomainEventEntity(
            eventStream = stream,
            payloadJson = roomEventJson.encodeToString(event),
            occurredAtEpochMs = event.timestamp.toEpochMilliseconds(),
        )
}

@OptIn(ExperimentalSerializationApi::class)
private val roomEventJson = Json {
    serializersModule = SerializersModule {
        polymorphic(DomainEvent::class) {
            subclassesOfSealed<AnalyticsEvent>()
            subclassesOfSealed<AccountEvent>()
            subclassesOfSealed<UserProductEvent>()
            subclassesOfSealed<SearchHistoryEvent>()
        }
    }
    explicitNulls = false
}
