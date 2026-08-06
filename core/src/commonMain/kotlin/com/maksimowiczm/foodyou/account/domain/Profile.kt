package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: ProfileId = ProfileId(Uuid.random()),
    val name: String,
    val avatar: Avatar,
    val homeCardsOrder: List<HomeCard> = HomeCard.defaultOrder,
    val favoriteFoods: Set<FavoriteFoodIdentity> = setOf(),
) {
    init {
        require(homeCardsOrder.distinct().size == homeCardsOrder.size) {
            "Home cards order cannot contain duplicates"
        }
    }

    @Serializable
    sealed interface Avatar {
        @Serializable data class Photo(val digest: BlobDigest) : Avatar

        @Serializable
        data class Predefined(val variant: Variant) : Avatar {
            @Serializable
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
