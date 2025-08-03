package com.maksimowiczm.foodyou.feature.fooddiary.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

internal class ExpandGoalsCard(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Boolean, Boolean>(
        dataStore = dataStore,
        key = booleanPreferencesKey("fooddiary:expand_goals_card")
    ) {
    override fun Boolean?.toValue(): Boolean = this ?: true
    override fun Boolean.toStore(): Boolean? = this
}
