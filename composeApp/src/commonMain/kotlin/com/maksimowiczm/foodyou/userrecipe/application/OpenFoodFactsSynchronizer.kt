package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductUpdatedEvent

class OpenFoodFactsSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<OpenFoodFactsProductUpdatedEvent> {
    override suspend fun handle(event: OpenFoodFactsProductUpdatedEvent) {
        userRecipeService.updateRecipesWithComponent(
            identity =
                FoodCompositionComponentIdentity.OpenFoodFacts(event.product.identity.barcode),
            name = event.product.name,
            nutritionFacts = event.product.nutritionFacts,
            servingWeight = (event.product.servingQuantity as? AbsoluteQuantity.Weight)?.weight,
            packageWeight = (event.product.packageQuantity as? AbsoluteQuantity.Weight)?.weight,
        )
    }
}
