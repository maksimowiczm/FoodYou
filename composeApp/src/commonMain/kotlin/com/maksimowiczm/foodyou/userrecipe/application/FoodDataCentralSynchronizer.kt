package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductUpdatedEvent

class FoodDataCentralSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<FoodDataCentralProductUpdatedEvent> {
    override suspend fun handle(event: FoodDataCentralProductUpdatedEvent) {
        userRecipeService.updateRecipesWithComponent(
            identity =
                FoodCompositionComponentIdentity.FoodDataCentral(event.product.identity.fdcId),
            name = FoodName(fallback = event.product.name),
            nutritionFacts = event.product.nutritionFacts,
            servingWeight = (event.product.servingQuantity as? AbsoluteQuantity.Weight)?.weight,
            packageWeight = (event.product.packageQuantity as? AbsoluteQuantity.Weight)?.weight,
        )
    }
}
