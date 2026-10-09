package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductUpdatedEvent
import com.maksimowiczm.foodyou.openfoodfacts.domain.toSnapshot

class OpenFoodFactsFoodDiarySynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<OpenFoodFactsProductUpdatedEvent> {
    override suspend fun handle(event: OpenFoodFactsProductUpdatedEvent) {
        foodDiaryService.updateEntriesUsing(
            snapshot = event.product.toSnapshot(),
            servingQuantity = event.product.servingQuantity,
            packageQuantity = event.product.packageQuantity,
        )
    }
}
