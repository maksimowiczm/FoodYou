package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCreatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductUpdatedEvent

class FoodDiaryProductSynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<UserProductEvent> {
    override suspend fun handle(event: UserProductEvent) {
        when (event) {
            is UserProductCreatedEvent -> Unit

            is UserProductUpdatedEvent ->
                foodDiaryService.updateEntriesWithComponent(
                    identity =
                        FoodCompositionComponentIdentity.UserProduct(event.product.identity.id),
                    name = event.product.name,
                    nutritionFacts = event.product.nutritionFacts,
                    servingWeight = event.product.servingQuantity?.forceWeight(),
                    packageWeight = event.product.packageQuantity?.forceWeight(),
                    image = event.product.image?.let(FoodCompositionComponentImage::Blob),
                )

            is UserProductDeletedEvent -> {
                val componentIdentity =
                    FoodCompositionComponentIdentity.UserProduct(event.identity.id)
                when (event.strategy) {
                    DeleteStrategy.Delete ->
                        foodDiaryService.removeComponentFromEntries(componentIdentity)
                    DeleteStrategy.Unlink ->
                        foodDiaryService.unlinkComponentFromEntries(componentIdentity)
                }
            }
        }
    }
}
