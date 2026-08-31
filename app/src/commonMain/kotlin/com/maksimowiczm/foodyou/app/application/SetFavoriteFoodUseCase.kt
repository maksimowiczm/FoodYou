package com.maksimowiczm.foodyou.app.application

import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodId
import com.maksimowiczm.foodyou.account.domain.addFavoriteFood
import com.maksimowiczm.foodyou.account.domain.removeFavoriteFood
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class SetFavoriteFoodUseCase(
    private val appProfileManager: AppProfileManager,
    private val accountService: AccountService,
) {
    suspend fun setFavoriteFood(id: FavoriteFoodId, isFavorite: Boolean) {
        val profileId = appProfileManager.observeAppProfileId().filterNotNull().first()
        if (isFavorite) accountService.update { addFavoriteFood(profileId, id) }
        else accountService.update { removeFavoriteFood(profileId, id) }
    }
}
