package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.testLocalAccountId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AccountTest {
    @Test
    fun create_account_with_no_profiles_fails() {
        assertFailsWith<IllegalArgumentException> {
            Account.of(
                localAccountId = testLocalAccountId(),
                settings = AccountSettings.default,
                profiles = emptyList(),
            )
        }
    }

    @Test
    fun create_account_with_profiles_succeeds() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.Predefined.Person)

        val account =
            Account.of(
                localAccountId = testLocalAccountId(),
                settings = AccountSettings.default,
                profiles = listOf(profile),
            )

        assertEquals(1, account.profiles.size)
        assertEquals("Test User", account.profiles[0].name)
    }

    @Test
    fun addProfile_duplicate_profile_fails() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.Predefined.Person)

        val account =
            Account.of(
                localAccountId = testLocalAccountId(),
                settings = AccountSettings.default,
                profiles = listOf(profile),
            )

        assertFailsWith<IllegalArgumentException> { account.addProfile(profile) }
    }

    @Test
    fun updateProfile_nonexistent_profile_fails() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.Predefined.Person)

        val account =
            Account.of(
                localAccountId = testLocalAccountId(),
                settings = AccountSettings.default,
                profiles = listOf(profile),
            )

        val nonExistentProfileId = ProfileId("non-existent-id")

        assertFailsWith<IllegalArgumentException> {
            account.updateProfile(nonExistentProfileId) { it }
        }
    }

    @Test
    fun removeProfile_last_profile_fails() {
        val profile = Profile.new(name = "Test User", avatar = Profile.Avatar.Predefined.Person)

        val account =
            Account.of(
                localAccountId = testLocalAccountId(),
                settings = AccountSettings.default,
                profiles = listOf(profile),
            )

        assertFailsWith<IllegalArgumentException> { account.removeProfile(profile.id) }
    }

    @Test
    fun removeProfile_nonexistent_profile_fails() {
        val profile1 = Profile.new(name = "User One", avatar = Profile.Avatar.Predefined.Person)
        val profile2 = Profile.new(name = "User Two", avatar = Profile.Avatar.Predefined.Person)

        val account =
            Account.of(
                localAccountId = testLocalAccountId(),
                settings = AccountSettings.default,
                profiles = listOf(profile1, profile2),
            )

        val nonExistentProfileId = ProfileId("non-existent-id")

        assertFailsWith<IllegalArgumentException> { account.removeProfile(nonExistentProfileId) }
    }

    @Test
    fun removeProfile_existing_profile_succeeds() {
        val profile1 = Profile.new(name = "User One", avatar = Profile.Avatar.Predefined.Person)
        val profile2 = Profile.new(name = "User Two", avatar = Profile.Avatar.Predefined.Person)

        val account =
            Account.of(
                localAccountId = testLocalAccountId(),
                settings = AccountSettings.default,
                profiles = listOf(profile1, profile2),
            )

        account.removeProfile(profile1.id)

        assertEquals(1, account.profiles.size)
        assertEquals(profile2.id, account.profiles[0].id)
    }
}
