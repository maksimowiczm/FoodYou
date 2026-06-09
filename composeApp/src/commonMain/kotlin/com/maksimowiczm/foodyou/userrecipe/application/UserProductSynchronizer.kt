package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCreatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductUpdatedEvent

class UserProductSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<UserProductEvent> {
    override suspend fun handle(event: UserProductEvent) {
        when (event) {
            is UserProductCreatedEvent -> {
                // No action needed for creation as no recipe can use it yet
            }

            is UserProductUpdatedEvent ->
                userRecipeService.updateRecipesWithComponent(
                    identity =
                        FoodCompositionComponentIdentity.UserProduct(event.product.identity.id),
                    name = event.product.name,
                    nutritionFacts = event.product.nutritionFacts,
                    servingWeight =
                        (event.product.servingQuantity as? AbsoluteQuantity.Weight)?.weight,
                    packageWeight =
                        (event.product.packageQuantity as? AbsoluteQuantity.Weight)?.weight,
                    image = event.product.image?.let(FoodCompositionComponentImage::Blob),
                )

            is UserProductDeletedEvent -> {
                userRecipeService.removeComponentFromRecipes(
                    identity = FoodCompositionComponentIdentity.UserProduct(event.identity.id)
                )
            }
        }
    }
}
