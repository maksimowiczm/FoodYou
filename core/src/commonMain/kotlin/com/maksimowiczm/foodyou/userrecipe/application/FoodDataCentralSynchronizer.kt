package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductUpdatedEvent
import com.maksimowiczm.foodyou.fooddatacentral.domain.toSnapshot

class FoodDataCentralSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<FoodDataCentralProductUpdatedEvent> {
    override suspend fun handle(event: FoodDataCentralProductUpdatedEvent) {
        userRecipeService.updateRecipesUsing(
            snapshot = event.product.toSnapshot(),
            servingQuantity = event.product.servingQuantity,
            packageQuantity = event.product.packageQuantity,
        )
    }
}
