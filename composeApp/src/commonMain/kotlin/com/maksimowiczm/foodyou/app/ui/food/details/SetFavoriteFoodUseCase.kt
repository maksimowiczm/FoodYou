package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.account.domain.addFavoriteFood
import com.maksimowiczm.foodyou.account.domain.removeFavoriteFood
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

internal class SetFavoriteFoodUseCase(
    private val appProfileManager: AppProfileManager,
    private val accountService: AccountService,
) {
    suspend fun setFavoriteFood(identity: FavoriteFoodIdentity, isFavorite: Boolean) {
        val profileId = appProfileManager.observeAppProfileId().filterNotNull().first()
        if (isFavorite) accountService.update { addFavoriteFood(profileId, identity) }
        else accountService.update { removeFavoriteFood(profileId, identity) }
    }
}
