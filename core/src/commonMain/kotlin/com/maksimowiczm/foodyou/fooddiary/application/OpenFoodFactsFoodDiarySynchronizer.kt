package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductUpdatedEvent
import com.maksimowiczm.foodyou.openfoodfacts.domain.toSnapshot

class OpenFoodFactsFoodDiarySynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<OpenFoodFactsProductUpdatedEvent> {
    override suspend fun handle(event: OpenFoodFactsProductUpdatedEvent) {
        foodDiaryService.updateEntriesUsing(
            snapshot = event.product.toSnapshot(),
            servingWeight = event.product.servingQuantity?.forceWeight(),
            packageWeight = event.product.packageQuantity?.forceWeight(),
        )
    }
}
