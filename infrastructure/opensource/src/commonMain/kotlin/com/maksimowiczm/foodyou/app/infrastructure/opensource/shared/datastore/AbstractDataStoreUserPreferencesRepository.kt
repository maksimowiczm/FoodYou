package com.maksimowiczm.foodyou.app.infrastructure.opensource.shared.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class AbstractDataStoreUserPreferencesRepository<P : UserPreferences>(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository<P> {
    override fun observe(): Flow<P> = dataStore.data.map { it.toUserPreferences() }

    override suspend fun update(transform: P.() -> P) {
        dataStore.edit { mutablePreferences ->
            val updated = mutablePreferences.toUserPreferences().transform()
            mutablePreferences.applyUserPreferences(updated)
        }
    }

    protected abstract fun Preferences.toUserPreferences(): P

    protected abstract fun MutablePreferences.applyUserPreferences(updated: P)
}
