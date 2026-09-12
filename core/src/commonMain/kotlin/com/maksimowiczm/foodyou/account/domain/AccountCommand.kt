package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import kotlin.time.Instant

sealed interface AccountCommand {
    data class FinishOnboarding(val timestamp: Instant) : AccountCommand

    data class ChangeEnergyUnit(val unit: EnergyUnit, val timestamp: Instant) : AccountCommand

    data class ChangeNutrientsOrder(val order: List<NutrientsOrder>, val timestamp: Instant) :
        AccountCommand

    data class ChangeEnableSingleProfileMode(val enable: Boolean, val timestamp: Instant) :
        AccountCommand

    data class AddProfile(val profile: Profile, val timestamp: Instant) : AccountCommand

    data class UpdateProfile(
        val profileId: ProfileId,
        val timestamp: Instant,
        val transform: (Profile) -> Profile,
    ) : AccountCommand

    data class RemoveProfile(val profileId: ProfileId, val timestamp: Instant) : AccountCommand

    data class AddFavoriteFood(
        val profileId: ProfileId,
        val foodId: FavoriteFoodId,
        val timestamp: Instant,
    ) : AccountCommand

    data class RemoveFavoriteFood(
        val profileId: ProfileId,
        val foodId: FavoriteFoodId,
        val timestamp: Instant,
    ) : AccountCommand

    data class RemoveFavoriteUserFood(val id: UserProductId, val timestamp: Instant) :
        AccountCommand
}
