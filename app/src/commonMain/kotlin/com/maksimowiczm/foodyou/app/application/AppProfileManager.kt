package com.maksimowiczm.foodyou.app.application

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * Manages the currently active profile for the application.
 *
 * This interface provides functionality to set and observe the application's current profile. It
 * serves as the central point for managing the active user context throughout the application
 * lifecycle.
 */
class AppProfileManager(
    private val accountService: AccountService,
    // It leaks DataStore into application layer, but this seems fine in application package
    private val dataStore: DataStore<Preferences>,
) {
    /**
     * Sets the currently active profile ID for the application.
     *
     * @param profileId The ID of the profile to set as active.
     */
    suspend fun setAppProfileId(profileId: ProfileId) {
        dataStore.updateData {
            it.toMutablePreferences().apply { set(profileIdKey, profileId.value.toString()) }
        }
    }

    /**
     * Observes changes to the currently active profile ID.
     *
     * @return A [Flow] that emits the current profile ID whenever it changes. Emits `null` if no
     *   profile is currently set as active.
     */
    fun observeAppProfileId(): Flow<ProfileId?> =
        dataStore.data.map { prefs -> prefs[profileIdKey]?.let(Uuid::parse)?.let(::ProfileId) }

    /**
     * Observes changes to the currently active profile.
     *
     * **Note:** This flow will block until a profile is set. If no profile has been set yet,
     * collectors will suspend indefinitely until [setAppProfileId] is called with a valid profile
     * ID.
     *
     * @return A [Flow] that emits the current [Profile] whenever it changes.
     */
    fun observeAppProfile(): Flow<Profile> =
        observeAppProfileId().filterNotNull().flatMapLatest { profileId ->
            accountService.observe().filterNotNull().mapNotNull { account ->
                account.profiles.find { it.id == profileId }
            }
        }

    private companion object {
        private val profileIdKey = stringPreferencesKey("profileId")
    }
}
