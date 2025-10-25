package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AccountTest {
    @Test
    fun create_account_with_no_profiles_fails() {
        assertFailsWith<IllegalArgumentException> {
            Account.of(
                localAccountId = LocalAccountId("test-id"),
                settings = AccountSettings.default,
                profiles = emptyList(),
            )
        }
    }

    @Test
    fun create_account_with_profiles_succeeds() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.PERSON)

        val account =
            Account.of(
                localAccountId = LocalAccountId("test-id"),
                settings = AccountSettings.default,
                profiles = listOf(profile),
            )

        assertEquals(1, account.profiles.size)
        assertEquals("Test User", account.profiles[0].name)
    }

    @Test
    fun addProfile_duplicate_profile_fails() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.PERSON)

        val account =
            Account.of(
                localAccountId = LocalAccountId("test-id"),
                settings = AccountSettings.default,
                profiles = listOf(profile),
            )

        assertFailsWith<IllegalArgumentException> { account.addProfile(profile) }
    }

    @Test
    fun updateProfile_nonexistent_profile_fails() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.PERSON)

        val account =
            Account.of(
                localAccountId = LocalAccountId("test-id"),
                settings = AccountSettings.default,
                profiles = listOf(profile),
            )

        val nonExistentProfileId = ProfileId("non-existent-id")

        assertFailsWith<IllegalArgumentException> {
            account.updateProfile(nonExistentProfileId) { it }
        }
    }
}
