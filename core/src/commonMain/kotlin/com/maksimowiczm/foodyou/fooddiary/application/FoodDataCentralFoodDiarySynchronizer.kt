package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductUpdatedEvent
import com.maksimowiczm.foodyou.fooddatacentral.domain.toSnapshot

class FoodDataCentralFoodDiarySynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<FoodDataCentralProductUpdatedEvent> {
    override suspend fun handle(event: FoodDataCentralProductUpdatedEvent) {
        foodDiaryService.updateEntriesUsing(
            snapshot = event.product.toSnapshot(),
            servingQuantity = event.product.servingQuantity,
            packageQuantity = event.product.packageQuantity,
        )
    }
}
