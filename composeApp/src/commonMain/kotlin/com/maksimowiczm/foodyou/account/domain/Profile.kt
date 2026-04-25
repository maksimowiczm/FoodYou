package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ImageUri
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid

data class Profile(
    val id: ProfileId = ProfileId(Uuid.random()),
    val name: String,
    val avatar: Avatar,
    val homeCardsOrder: List<HomeCard> = HomeCard.defaultOrder,
    val favoriteFoods: Set<FavoriteFoodIdentity> = setOf(),
) {
    sealed interface Avatar {
        data class Photo(val uri: ImageUri) : Avatar

        sealed interface Predefined : Avatar {

            data object Person : Predefined

            data object Woman : Predefined

            data object Man : Predefined

            data object Engineer : Predefined
        }
    }
}
