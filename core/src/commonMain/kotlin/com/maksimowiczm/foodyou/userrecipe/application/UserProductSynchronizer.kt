package com.maksimowiczm.foodyou.userrecipe.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCreatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductUpdatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.toSnapshot

class UserProductSynchronizer(private val userRecipeService: UserRecipeService) :
    EventHandler<UserProductEvent> {
    override suspend fun handle(event: UserProductEvent) {
        when (event) {
            is UserProductCreatedEvent -> Unit

            is UserProductUpdatedEvent ->
                userRecipeService.updateRecipesUsing(
                    snapshot = event.product.toSnapshot(),
                    servingWeight = event.product.servingQuantity?.forceWeight(),
                    packageWeight = event.product.packageQuantity?.forceWeight(),
                )

            is UserProductDeletedEvent -> {
                val componentIdentity = FoodSnapshotId.UserProduct(event.identity.id)
                when (event.strategy) {
                    DeleteStrategy.Delete ->
                        userRecipeService.removeComponentFromRecipes(componentIdentity)
                    DeleteStrategy.Unlink ->
                        userRecipeService.unlinkComponentFromRecipes(componentIdentity)
                }
            }
        }
    }
}
