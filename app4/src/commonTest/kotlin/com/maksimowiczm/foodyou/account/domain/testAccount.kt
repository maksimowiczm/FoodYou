package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId

fun testLocalAccountId(id: String = "test-account-id"): LocalAccountId = LocalAccountId(id)

fun testAccount(
    localAccountId: LocalAccountId = testLocalAccountId(),
    settings: AccountSettings = AccountSettings.default,
    profiles: List<Profile> = listOf(testProfile()),
): Account = Account.of(localAccountId = localAccountId, settings = settings, profiles = profiles)
