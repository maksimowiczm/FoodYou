package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductUpdatedEvent

class FoodDataCentralFoodDiarySynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<FoodDataCentralProductUpdatedEvent> {
    override suspend fun handle(event: FoodDataCentralProductUpdatedEvent) {
        foodDiaryService.updateEntriesWithComponent(
            identity =
                FoodCompositionComponentIdentity.FoodDataCentral(event.product.identity.fdcId),
            name = FoodName(fallback = event.product.name),
            servingWeight = event.product.servingQuantity?.forceWeight(),
            packageWeight = event.product.packageQuantity?.forceWeight(),
            nutritionFacts = event.product.nutritionFacts,
        )
    }
}
