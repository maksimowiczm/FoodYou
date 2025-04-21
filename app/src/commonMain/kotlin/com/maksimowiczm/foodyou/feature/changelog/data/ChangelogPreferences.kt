package com.maksimowiczm.foodyou.feature.changelog.data

import androidx.datastore.preferences.core.stringPreferencesKey

internal object ChangelogPreferences {
    val latestRememberedVersion = stringPreferencesKey("latest_remembered_version")
}
