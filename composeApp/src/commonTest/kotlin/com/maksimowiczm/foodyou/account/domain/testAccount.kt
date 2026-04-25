package com.maksimowiczm.foodyou.account.domain

fun testAccount(
    settings: AccountSettings = AccountSettings(),
    profiles: List<Profile> = listOf(testProfile()),
): Account = Account(settings = settings, profiles = profiles)
