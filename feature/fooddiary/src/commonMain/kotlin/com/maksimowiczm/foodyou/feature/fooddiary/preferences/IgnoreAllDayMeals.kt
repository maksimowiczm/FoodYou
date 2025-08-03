package com.maksimowiczm.foodyou.feature.fooddiary.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

internal class IgnoreAllDayMeals(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Boolean, Boolean>(
        dataStore = dataStore,
        key = booleanPreferencesKey("fooddiary:ignore_all_day_meals")
    ) {
    override fun Boolean?.toValue(): Boolean = this ?: false
    override fun Boolean.toStore(): Boolean? = this
}
