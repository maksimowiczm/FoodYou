package com.maksimowiczm.foodyou.account.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.LocalAccountId
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.ProfileId
import com.maksimowiczm.foodyou.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class AccountManagerImpl(
    private val dataStore: DataStore<Preferences>,
    private val accountRepository: AccountRepository,
) : AccountManager {
    private fun observePrimaryAccount(): Flow<Account?> =
        dataStore.data.flatMapLatest { prefs ->
            val id = prefs[AccountManagerKeys.primaryAccountId]?.let(::LocalAccountId)

            if (id == null) {
                return@flatMapLatest flowOf(null)
            }

            accountRepository.observe(id)
        }

    override suspend fun setPrimaryAccountId(
        accountId: LocalAccountId
    ): Result<Unit, AccountManager.Error.AccountNotFound> {
        val account = accountRepository.load(accountId)

        if (account == null) {
            return Err(AccountManager.Error.AccountNotFound)
        }

        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[AccountManagerKeys.primaryAccountId] = accountId.value
            }
        }

        return Ok()
    }

    override fun observePrimaryAccountId(): Flow<LocalAccountId?> =
        observePrimaryAccount().map { it?.localAccountId }

    override suspend fun setPrimaryProfileId(
        profileId: ProfileId
    ): Result<Unit, AccountManager.Error> {
        val account = observePrimaryAccount().first()

        if (account == null) {
            return Err(AccountManager.Error.AccountNotFound)
        }

        if (!account.profiles.any { it.id == profileId }) {
            return Err(AccountManager.Error.ProfileNotFound)
        }

        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[AccountManagerKeys.primaryProfileId] = profileId.value
            }
        }

        return Ok()
    }

    override fun observePrimaryProfileId(): Flow<ProfileId> {
        return observePrimaryAccount().filterNotNull().flatMapLatest { account ->
            dataStore.data.map { prefs ->
                val id = prefs[AccountManagerKeys.primaryProfileId]?.let(::ProfileId)

                if (id == null) {
                    return@map account.defaultProfile.id
                }

                // If profile no longer exists, return the first one.
                if (!account.profiles.any { it.id == id }) {
                    return@map account.defaultProfile.id
                }

                id
            }
        }
    }
}

private object AccountManagerKeys {
    val primaryAccountId = stringPreferencesKey("account:account_manager:primary_account_id")
    val primaryProfileId = stringPreferencesKey("account:account_manager:primary_profile_id")
}
