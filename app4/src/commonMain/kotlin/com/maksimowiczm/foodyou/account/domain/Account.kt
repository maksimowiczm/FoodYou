package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlin.uuid.Uuid

class Account(val localAccountId: LocalAccountId, settings: Settings, profiles: List<Profile>) {
    companion object {
        fun new(): Account {
            val id = Uuid.random()

            return Account(
                localAccountId = LocalAccountId(id.toString()),
                settings = Settings.default,
                profiles = emptyList(),
            )
        }
    }

    var settings: Settings = settings
        private set

    private val _profiles: MutableList<Profile> = profiles.toMutableList()
    val profiles: List<Profile>
        get() = _profiles.toList()

    fun addProfile(profile: Profile) {
        _profiles.add(profile)
    }

    fun updateSettings(transform: (Settings) -> Settings) {
        settings = transform(settings)
    }
}
