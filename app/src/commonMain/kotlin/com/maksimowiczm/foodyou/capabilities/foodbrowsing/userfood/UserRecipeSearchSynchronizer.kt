package com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCreatedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeEvent
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeUpdatedEvent

class UserRecipeSearchSynchronizer(private val searchDao: SearchDao) :
    EventHandler<UserRecipeEvent> {
    override suspend fun handle(event: UserRecipeEvent) {
        when (event) {
            is UserRecipeCreatedEvent ->
                searchDao.upsert(SearchResultMapper.toSearchEntity(event.recipe))
            is UserRecipeUpdatedEvent ->
                searchDao.upsert(SearchResultMapper.toSearchEntity(event.recipe))
            is UserRecipeDeletedEvent -> searchDao.deleteByRecipeId(event.userRecipeId.value)
        }
    }
}
