package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlin.uuid.Uuid

class Account
private constructor(
    val localAccountId: LocalAccountId,
    settings: AccountSettings,
    profiles: List<Profile>,
) {
    companion object {
        fun create(primaryProfile: Profile): Account {
            val uuid = Uuid.random()
            val id = LocalAccountId(uuid.toString())
            return Account(
                localAccountId = id,
                settings = AccountSettings.default,
                profiles = listOf(primaryProfile),
            )
        }

        fun of(
            localAccountId: LocalAccountId,
            settings: AccountSettings,
            profiles: List<Profile>,
        ): Account =
            Account(localAccountId = localAccountId, settings = settings, profiles = profiles)
    }

    init {
        require(profiles.isNotEmpty()) { "Account must have at least one profile" }
    }

    var settings: AccountSettings = settings
        private set

    private val _profiles: MutableList<Profile> = profiles.toMutableList()
    val profiles: List<Profile>
        get() = _profiles.toList()

    val defaultProfile: Profile
        get() = _profiles.first()

    fun updateSettings(transform: (AccountSettings) -> AccountSettings) {
        settings = transform(settings)
    }
}
