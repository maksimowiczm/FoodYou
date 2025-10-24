package com.maksimowiczm.foodyou.account.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProfileTest {
    @Test
    fun updateHomeCardsOrder_withAllCards_updatesOrder() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.PERSON)

        val newOrder = profile.homeCardsOrder.reversed()
        profile.updateHomeCardsOrder(newOrder)

        assertEquals(
            newOrder,
            profile.homeCardsOrder,
            "Home cards order should be updated correctly",
        )
    }

    @Test
    fun updateHomeCardsOrder_withMissingCard_throwsError() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.PERSON)

        val invalidOrder = profile.homeCardsOrder.drop(1) // Remove one card to make it invalid

        assertFailsWith<IllegalStateException> { profile.updateHomeCardsOrder(invalidOrder) }
    }
}
