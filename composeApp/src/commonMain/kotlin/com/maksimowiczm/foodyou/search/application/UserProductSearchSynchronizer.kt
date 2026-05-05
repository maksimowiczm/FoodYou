package com.maksimowiczm.foodyou.search.application

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCreatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductUpdatedEvent

class UserProductSearchSynchronizer(private val searchRepository: SearchRepository) :
    EventHandler<UserProductEvent> {
    override suspend fun handle(event: UserProductEvent) {
        when (event) {
            is UserProductCreatedEvent ->
                searchRepository.save(
                    SearchResult.UserProduct(
                        identity = event.product.identity,
                        name = event.product.name,
                        brand = event.product.brand,
                        barcode = event.product.barcode,
                        note = event.product.note,
                        image = event.product.image,
                        nutritionFacts = event.product.nutritionFacts,
                        servingQuantity = event.product.servingQuantity,
                        packageQuantity = event.product.packageQuantity,
                        isLiquid = event.product.isLiquid,
                    )
                )

            is UserProductUpdatedEvent ->
                searchRepository.save(
                    SearchResult.UserProduct(
                        identity = event.product.identity,
                        name = event.product.name,
                        brand = event.product.brand,
                        barcode = event.product.barcode,
                        note = event.product.note,
                        image = event.product.image,
                        nutritionFacts = event.product.nutritionFacts,
                        servingQuantity = event.product.servingQuantity,
                        packageQuantity = event.product.packageQuantity,
                        isLiquid = event.product.isLiquid,
                    )
                )

            is UserProductDeletedEvent -> searchRepository.delete(event.identity)
        }
    }
}
