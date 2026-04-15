package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

internal object RoomStoredEventMapper {
    fun toDomainEvent(entity: RoomStoredEventEntity): DomainEvent =
        roomEventJson.decodeFromString(entity.payloadJson)

    fun toRoomStoredEventEntity(event: DomainEvent, stream: String): RoomStoredEventEntity =
        RoomStoredEventEntity(
            eventStream = stream,
            payloadJson = roomEventJson.encodeToString(event),
            occurredAtEpochMs = event.timestamp.toEpochMilliseconds(),
        )
}

@OptIn(ExperimentalSerializationApi::class)
private val roomEventJson = Json {
    serializersModule = SerializersModule {
        polymorphic(DomainEvent::class) { subclassesOfSealed<AnalyticsEvent>() }
    }
}
