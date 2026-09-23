package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductUpdatedEvent
import com.maksimowiczm.foodyou.openfoodfacts.domain.toSnapshot

class OpenFoodFactsSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<OpenFoodFactsProductUpdatedEvent> {
    override suspend fun handle(event: OpenFoodFactsProductUpdatedEvent) {
        userRecipeService.updateRecipesUsing(
            snapshot = event.product.toSnapshot(),
            servingWeight = event.product.servingQuantity?.forceWeight(),
            packageWeight = event.product.packageQuantity?.forceWeight(),
        )
    }
}
