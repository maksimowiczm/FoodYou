package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId

fun testProfileId(id: String = "test-profile-id"): ProfileId = ProfileId(id)

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
