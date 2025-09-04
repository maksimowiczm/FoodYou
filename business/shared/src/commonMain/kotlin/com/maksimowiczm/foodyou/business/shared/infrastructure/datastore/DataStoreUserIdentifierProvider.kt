package com.maksimowiczm.foodyou.business.shared.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.shared.domain.identity.UserIdentifier
import com.maksimowiczm.foodyou.business.shared.domain.identity.UserIdentifierProvider
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * An implementation of [UserIdentifierProvider] that retrieves the user identifier from a DataStore
 */
@OptIn(ExperimentalUuidApi::class)
internal class DataStoreUserIdentifierProvider(private val dataStore: DataStore<Preferences>) :
    UserIdentifierProvider {
    override suspend fun getUserIdentifier(): UserIdentifier {
        val id = dataStore.data.map { prefs -> prefs[PreferencesKeys.userIdentifier] }.first()

        if (id == null) {
            val newId = Uuid.random()
            dataStore.updateData { prefs ->
                prefs.toMutablePreferences().apply {
                    this[PreferencesKeys.userIdentifier] = newId.toString()
                }
            }
            return UserIdentifier(newId.toString())
        }

        return UserIdentifier(id)
    }
}

private object PreferencesKeys {
    val userIdentifier = stringPreferencesKey("user_identifier")
}
