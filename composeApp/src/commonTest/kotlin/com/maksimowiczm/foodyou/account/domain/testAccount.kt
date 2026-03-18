package com.maksimowiczm.foodyou.account.domain

fun testAccount(
    settings: AccountSettings = AccountSettings.default,
    profiles: List<Profile> = listOf(testProfile()),
): Account = Account.of(settings = settings, profiles = profiles)
