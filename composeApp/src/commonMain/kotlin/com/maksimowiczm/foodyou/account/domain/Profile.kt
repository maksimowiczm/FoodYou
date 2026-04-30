package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.BlobDigest
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
        data class Photo(val digest: BlobDigest) : Avatar

        data class Predefined(val variant: Variant) : Avatar {
            enum class Variant {
                Person,
                Woman,
                Man,
                Engineer;

                fun toAvatar() = Predefined(this)
            }
        }
    }
}
