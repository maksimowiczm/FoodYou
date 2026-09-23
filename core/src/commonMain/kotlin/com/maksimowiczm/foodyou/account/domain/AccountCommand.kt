package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId

sealed interface AccountCommand {
    data object FinishOnboarding : AccountCommand

    data class ChangeEnableSingleProfileMode(val enable: Boolean) : AccountCommand

    data class AddProfile(val profile: Profile) : AccountCommand

    data class UpdateProfile(
        val profileId: ProfileId,
        val transform: (Profile) -> Profile,
    ) : AccountCommand

    data class RemoveProfile(val profileId: ProfileId) : AccountCommand

    data class AddFavoriteFood(
        val profileId: ProfileId,
        val foodId: FavoriteFoodId,
    ) : AccountCommand

    data class RemoveFavoriteFood(
        val profileId: ProfileId,
        val foodId: FavoriteFoodId,
    ) : AccountCommand

    data class RemoveFavoriteUserFood(val id: UserProductId) : AccountCommand
}
