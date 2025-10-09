package com.maksimowiczm.foodyou.common.infrastructure.room.eventstore

import com.maksimowiczm.foodyou.analytics.domain.AppLaunchedEvent
import com.maksimowiczm.foodyou.analytics.domain.AppVersionChangedEvent
import com.maksimowiczm.foodyou.analytics.domain.FirstAppLaunchRecordedEvent
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer

object RoomEventStoreMapper {
    fun toDomainEvent(entity: RoomEventStoreEntity): DomainEvent {
        return DomainEventSerializer.deserialize(entity.eventType, entity.eventData)
    }

    fun toRoomEventStoreEntity(event: DomainEvent): RoomEventStoreEntity {
        return RoomEventStoreEntity(
            aggregateId = event.aggregateId,
            eventType = event::class.simpleName ?: error("Event class must have a name"),
            eventData = DomainEventSerializer.serialize(event),
            timestamp = event.timestamp.toEpochMilliseconds(),
        )
    }
}

@OptIn(InternalSerializationApi::class)
private object DomainEventSerializer {
    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(DomainEvent::class) {
                subclass(AppLaunchedEvent::class, AppLaunchedEvent.serializer())
                subclass(AppVersionChangedEvent::class, AppVersionChangedEvent.serializer())
                subclass(
                    FirstAppLaunchRecordedEvent::class,
                    FirstAppLaunchRecordedEvent.serializer(),
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : DomainEvent> serialize(event: E): String {
        val serializer = event::class.serializer() as KSerializer<E>

        return json.encodeToString(serializer, event)
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : DomainEvent> deserialize(eventType: String, eventData: String): E {
        val eventClass =
            when (eventType) {
                AppLaunchedEvent::class.simpleName -> AppLaunchedEvent::class
                AppVersionChangedEvent::class.simpleName -> AppVersionChangedEvent::class
                FirstAppLaunchRecordedEvent::class.simpleName -> FirstAppLaunchRecordedEvent::class
                else -> error("Unknown event type: $eventType")
            }

        val deserializer = eventClass.serializer() as KSerializer<E>
        return json.decodeFromString(deserializer, eventData)
    }
}
