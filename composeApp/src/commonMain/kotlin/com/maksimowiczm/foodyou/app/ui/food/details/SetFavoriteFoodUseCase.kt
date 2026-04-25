package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.account.domain.update
import com.maksimowiczm.foodyou.account.domain.updateProfile
import com.maksimowiczm.foodyou.app.application.AppProfileManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

internal class SetFavoriteFoodUseCase(
    private val appProfileManager: AppProfileManager,
    private val accountRepository: AccountRepository,
) {
    suspend fun setFavoriteFood(identity: FavoriteFoodIdentity, isFavorite: Boolean) {
        val profileId = appProfileManager.observeAppProfileId().filterNotNull().first()
        accountRepository.update {
            updateProfile(profileId) {
                if (isFavorite) it.copy(favoriteFoods = it.favoriteFoods + identity)
                else it.copy(favoriteFoods = it.favoriteFoods - identity)
            }
        }
    }
}
