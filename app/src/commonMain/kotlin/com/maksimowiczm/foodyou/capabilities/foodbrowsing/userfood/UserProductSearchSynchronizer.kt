package com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCreatedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductDeletedEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductImportedFoodYou3Event
import com.maksimowiczm.foodyou.userproduct.domain.UserProductUpdatedEvent

class UserProductSearchSynchronizer(private val searchDao: SearchDao) :
    EventHandler<UserProductEvent> {
    override suspend fun handle(event: UserProductEvent) {
        when (event) {
            is UserProductCreatedEvent ->
                searchDao.upsert(SearchResultMapper.toSearchEntity(event.product))
            is UserProductImportedFoodYou3Event ->
                searchDao.upsert(SearchResultMapper.toSearchEntity(event.product))
            is UserProductUpdatedEvent ->
                searchDao.upsert(SearchResultMapper.toSearchEntity(event.product))
            is UserProductDeletedEvent -> searchDao.deleteByProductId(event.userProductId.value)
        }
    }
}
