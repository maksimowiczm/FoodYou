package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.application.AppAccountManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

internal class SetFavoriteFoodUseCase(
    private val appAccountManager: AppAccountManager,
    private val accountRepository: AccountRepository,
) {
    suspend fun setFavoriteFood(identity: FavoriteFoodIdentity, isFavorite: Boolean) {
        val account = appAccountManager.observeAppAccount().first()
        val profileId = appAccountManager.observeAppProfileId().filterNotNull().first()

        account.updateProfile(profileId) {
            it.apply {
                if (isFavorite) {
                    addFavoriteFood(identity)
                } else {
                    removeFavoriteFood(identity)
                }
            }
        }

        accountRepository.save(account)
    }
}
