package com.maksimowiczm.foodyou.feature.security.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object SecurityPreferences {
    val hideContent = booleanPreferencesKey("security_hide_content")
}
