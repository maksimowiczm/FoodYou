package com.maksimowiczm.foodyou.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppPreferences {
    // Rename calories card to goals card, update preference version
    val homeOrder = stringPreferencesKey("home_order_v2")

    val latestRememberedVersion = stringPreferencesKey("latest_remembered_version")

    val hideContent = booleanPreferencesKey("security_hide_content")
}
