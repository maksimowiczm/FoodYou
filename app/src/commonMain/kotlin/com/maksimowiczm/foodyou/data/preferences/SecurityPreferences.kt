package com.maksimowiczm.foodyou.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

object SecurityPreferences {
    val hideContent = booleanPreferencesKey("security_hide_content")
}
