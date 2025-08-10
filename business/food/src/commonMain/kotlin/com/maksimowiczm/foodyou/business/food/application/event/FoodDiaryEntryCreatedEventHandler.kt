package com.maksimowiczm.foodyou.business.food.application.event

import com.maksimowiczm.foodyou.business.shared.application.event.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.EventHandler
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger

internal class FoodDiaryEntryCreatedEventHandler : EventHandler<FoodDiaryEntryCreatedEvent> {
    override suspend fun handle(event: FoodDiaryEntryCreatedEvent) {
        FoodYouLogger.d("TAG") { "I'm alive, but at what cost?" }
    }
}
