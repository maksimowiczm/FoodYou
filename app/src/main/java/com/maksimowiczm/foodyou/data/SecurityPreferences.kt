package com.maksimowiczm.foodyou.data

import androidx.datastore.preferences.core.booleanPreferencesKey

object SecurityPreferences {
    val showContent = booleanPreferencesKey("security_show_content")
}
