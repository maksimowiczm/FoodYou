package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
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

    /** Updates a profile with the given [id] by applying the [transform] function. */
    fun updateProfile(id: ProfileId, transform: (Profile) -> Profile) {
        val index = _profiles.indexOfFirst { it.id == id }
        require(index != -1) { "Profile with id $id not found" }

        val updatedProfile = transform(_profiles[index])
        _profiles[index] = updatedProfile
    }

    fun addProfile(profile: Profile) {
        require(!_profiles.any { it.id == profile.id }) {
            "Profile with id ${profile.id} already exists"
        }

        _profiles.add(profile)
    }

    fun removeProfile(id: ProfileId) {
        require(_profiles.size > 1) { "Cannot remove the last profile from the account" }

        val index = _profiles.indexOfFirst { it.id == id }
        require(index != -1) { "Profile with id $id not found" }

        _profiles.removeAt(index)
    }

    fun removeFavoriteUserFood(identity: UserFoodProductIdentity) {
        _profiles.forEach {
            it.removeFavoriteFood(FavoriteFoodIdentity.UserFoodProduct(identity.id))
        }
    }
}
