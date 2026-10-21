package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCreatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductImportedFoodYou3Event
import com.maksimowiczm.foodyou.userproduct.domain.UserProductUpdatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.toSnapshot

class FoodDiaryProductSynchronizer(private val foodDiaryService: FoodDiaryService) :
    EventHandler<UserProductEvent> {
    override suspend fun handle(event: UserProductEvent) {
        when (event) {
            is UserProductCreatedEvent -> Unit

            is UserProductImportedFoodYou3Event -> Unit

            is UserProductUpdatedEvent ->
                foodDiaryService.updateEntriesUsing(
                    snapshot = event.product.toSnapshot(),
                    servingQuantity = event.product.servingQuantity,
                    packageQuantity = event.product.packageQuantity,
                )

            is UserProductDeletedEvent -> {
                val snapshotId = FoodSnapshotId.UserProduct(event.userProductId.value)
                when (event.strategy) {
                    DeleteStrategy.Delete -> foodDiaryService.removeComponentFromEntries(snapshotId)
                    DeleteStrategy.Unlink -> foodDiaryService.unlinkComponentFromEntries(snapshotId)
                }
            }
        }
    }
}
