package com.maksimowiczm.foodyou.account.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

fun AccountManager.observePrimaryProfile(account: Account): Flow<Profile> =
    observePrimaryProfileId()
        .map { profileId -> account.profiles.find { it.id == profileId } }
        .filterNotNull()
