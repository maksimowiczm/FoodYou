package com.maksimowiczm.foodyou.feature.security.data

import androidx.datastore.preferences.core.booleanPreferencesKey

object SecurityPreferences {
    val hideContent = booleanPreferencesKey("security_hide_content")
}
