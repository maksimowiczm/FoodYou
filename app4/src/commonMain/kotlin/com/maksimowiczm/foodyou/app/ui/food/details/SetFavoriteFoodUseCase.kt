package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import kotlinx.coroutines.flow.first

internal class SetFavoriteFoodUseCase(
    private val accountManager: AccountManager,
    private val accountRepository: AccountRepository,
    private val observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
) {
    suspend fun setFavoriteFood(identity: FavoriteFoodIdentity, isFavorite: Boolean) {
        val account = observePrimaryAccountUseCase.observe().first()
        val profileId = accountManager.observePrimaryProfileId().first()

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
