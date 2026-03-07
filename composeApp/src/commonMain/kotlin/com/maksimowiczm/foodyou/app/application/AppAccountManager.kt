package com.maksimowiczm.foodyou.app.application

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.account.domain.Account
import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Manages the currently active account and profile for the application.
 *
 * This interface provides functionality to set and observe the application's current account and
 * profile. It serves as the central point for managing the active user context throughout the
 * application lifecycle.
 */
class AppAccountManager(
    private val accountRepository: AccountRepository,
    // It leaks DataStore into application layer, but this seems fine in application package
    private val dataStore: DataStore<Preferences>,
) {
    /**
     * Sets the currently active account ID for the application.
     *
     * This operation updates the active account context. All subsequent operations that depend on
     * the current account will use this account ID.
     *
     * @param accountId The ID of the account to set as active.
     */
    suspend fun setAppAccountId(accountId: LocalAccountId) {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                set(AppAccountManagerKeys.accountId, accountId.value)
            }
        }
    }

    /**
     * Observes changes to the currently active account ID.
     *
     * @return A [Flow] that emits the current account ID whenever it changes. Emits `null` if no
     *   account is currently set as active.
     */
    fun observeAppAccountId(): Flow<LocalAccountId?> =
        dataStore.data.map { prefs ->
            prefs[AppAccountManagerKeys.accountId]?.let(::LocalAccountId)
        }

    /**
     * Observes changes to the currently active account.
     *
     * This flow automatically updates when the active account ID changes or when the account's data
     * is modified.
     *
     * **Note:** This flow will block until an account is set. If no account has been set yet,
     * collectors will suspend indefinitely until [setAppAccountId] is called with a valid account
     * ID.
     *
     * @return A [Flow] that emits the current [Account] whenever it changes.
     */
    fun observeAppAccount(): Flow<Account> =
        observeAppAccountId()
            .filterNotNull()
            .flatMapLatest(accountRepository::observe)
            .filterNotNull()

    /**
     * Sets the currently active profile ID for the application.
     *
     * This operation updates the active profile context. All subsequent operations that depend on
     * the current profile will use this profile ID.
     *
     * @param profileId The ID of the profile to set as active.
     */
    suspend fun setAppProfileId(profileId: ProfileId) {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                set(AppAccountManagerKeys.profileId, profileId.value)
            }
        }
    }

    /**
     * Observes changes to the currently active profile ID.
     *
     * @return A [Flow] that emits the current profile ID whenever it changes. Emits `null` if no
     *   profile is currently set as active.
     */
    fun observeAppProfileId(): Flow<ProfileId?> =
        dataStore.data.map { prefs -> prefs[AppAccountManagerKeys.profileId]?.let(::ProfileId) }

    /**
     * Observes changes to the currently active profile.
     *
     * This flow automatically updates when the active profile ID changes or when the profile's data
     * is modified.
     *
     * **Note:** This flow will block until a profile is set. If no profile has been set yet,
     * collectors will suspend indefinitely until [setAppProfileId] is called with a valid profile
     * ID.
     *
     * @return A [Flow] that emits the current [Profile] whenever it changes.
     */
    fun observeAppProfile(): Flow<Profile> =
        observeAppProfileId().filterNotNull().flatMapLatest { profileId ->
            observeAppAccount()
                .map { account -> account.profiles.find { it.id == profileId } }
                .filterNotNull()
        }
}

private object AppAccountManagerKeys {
    val accountId = stringPreferencesKey("app:app_account_manager:account_id")
    val profileId = stringPreferencesKey("app:app_account_manager:profile_id")
}
