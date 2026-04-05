package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid

fun testProfileId(id: Uuid = Uuid.random()): ProfileId = ProfileId(id)

fun testProfile(
    id: ProfileId = testProfileId(),
    name: String = "Test User",
    avatar: Profile.Avatar = Profile.Avatar.Predefined.Person,
    homeCardsOrder: List<HomeCard> = HomeCard.defaultOrder,
    favoriteFoods: List<FavoriteFoodIdentity> = listOf(),
): Profile =
    Profile(
        id = id,
        name = name,
        avatar = avatar,
        homeCardsOrder = homeCardsOrder,
        favoriteFoods = favoriteFoods,
    )
