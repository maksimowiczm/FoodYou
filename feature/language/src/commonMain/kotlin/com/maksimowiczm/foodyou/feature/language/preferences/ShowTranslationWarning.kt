package com.maksimowiczm.foodyou.feature.language.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

class ShowTranslationWarning(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Boolean, Boolean>(
        dataStore = dataStore,
        key = booleanPreferencesKey("show_translation_warning")
    ) {
    override fun Boolean?.toValue(): Boolean = this ?: true
    override fun Boolean.toStore(): Boolean? = this
}
