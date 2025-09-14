package com.maksimowiczm.foodyou.app.infrastructure.opensource.poll

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollId
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.PollPreferences
import com.maksimowiczm.foodyou.app.infrastructure.opensource.shared.datastore.AbstractDataStoreUserPreferencesRepository

internal class DataStorePollPreferencesRepository(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<PollPreferences>(dataStore) {
    override fun Preferences.toUserPreferences(): PollPreferences {
        val dismissedPolls = this[PollPreferencesKeys.dismissedPools] ?: emptySet()
        return PollPreferences(dismissedPolls.map(String::toPollId).toSet())
    }

    override fun MutablePreferences.applyUserPreferences(updated: PollPreferences) {
        this[PollPreferencesKeys.dismissedPools] = updated.dismissedPolls.map { it.value }.toSet()
    }
}

private fun String.toPollId(): PollId = PollId(this)

private object PollPreferencesKeys {
    val dismissedPools = stringSetPreferencesKey("dismissed_polls")
}
