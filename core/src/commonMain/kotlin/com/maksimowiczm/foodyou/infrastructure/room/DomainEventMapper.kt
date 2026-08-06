package com.maksimowiczm.foodyou.infrastructure.room

import com.maksimowiczm.foodyou.account.domain.AccountEvent
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent
import com.maksimowiczm.foodyou.mealplan.domain.MealPlanEvent
import com.maksimowiczm.foodyou.search.domain.SearchHistoryEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object DomainEventMapper {
    fun toDomainEvent(entity: DomainEventEntity): DomainEvent =
        roomEventJson.decodeFromString(
            PolymorphicSerializer(DomainEvent::class),
            entity.payloadJson,
        )

    fun toRoomStoredEventEntity(event: DomainEvent, stream: String): DomainEventEntity =
        DomainEventEntity(
            id = event.id,
            eventStream = stream,
            payloadJson =
                roomEventJson.encodeToString(PolymorphicSerializer(DomainEvent::class), event),
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
            subclassesOfSealed<UserRecipeEvent>()
            subclassesOfSealed<SearchHistoryEvent>()
            subclassesOfSealed<FoodDiaryEvent>()
            subclassesOfSealed<MealPlanEvent>()
        }
    }
    explicitNulls = false
}
