package com.maksimowiczm.foodyou.account.domain

import kotlin.test.Test
import kotlin.test.assertFailsWith

class AccountTest {
    @Test
    fun disallow_empty_profiles() {
        assertFailsWith<IllegalArgumentException> { Account(profiles = emptyList()) }
    }

    @Test
    fun disallow_duplicate_profiles() {
        val profileId = testProfileId()

        assertFailsWith<IllegalArgumentException> {
            Account(profiles = listOf(testProfile(id = profileId), testProfile(id = profileId)))
        }
    }

    @Test
    fun disallow_missing_home_cards() {
        val profile = testProfile(homeCardsOrder = HomeCard.defaultOrder.drop(1))
        assertFailsWith<IllegalArgumentException> { Account(profiles = listOf(profile)) }
    }
}
