package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.LocalAccountId
import kotlin.uuid.Uuid

class Account private constructor(val localAccountId: LocalAccountId) {
    companion object {
        fun create(): Account {
            val uuid = Uuid.random()
            val id = LocalAccountId(uuid.toString())
            return Account(localAccountId = id)
        }

        fun of(localAccountId: LocalAccountId): Account = Account(localAccountId = localAccountId)

        fun of(
            localAccountId: LocalAccountId,
            settings: Settings,
            profiles: List<Profile>,
        ): Account =
            Account(localAccountId = localAccountId).apply {
                this.settings = settings
                this._profiles += profiles
            }
    }

    var settings: Settings = Settings.default
        private set

    private val _profiles: MutableList<Profile> = mutableListOf()
    val profiles: List<Profile>
        get() = _profiles.toList()

    fun addProfile(profile: Profile) {
        _profiles.add(profile)
    }

    fun updateSettings(transform: (Settings) -> Settings) {
        settings = transform(settings)
    }
}
