package com.maksimowiczm.foodyou.capabilities.fooddetails

import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.AccountCommand
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodId
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class SetFavoriteFoodUseCase(
    private val appProfileManager: AppProfileManager,
    private val accountService: AccountService,
) {
    suspend fun setFavoriteFood(id: FavoriteFoodId, isFavorite: Boolean) {
        val profileId = appProfileManager.observeAppProfileId().filterNotNull().first()
        if (isFavorite) {
            accountService.handle(AccountCommand.AddFavoriteFood(profileId, id))
        } else {
            accountService.handle(AccountCommand.RemoveFavoriteFood(profileId, id))
        }
    }
}
