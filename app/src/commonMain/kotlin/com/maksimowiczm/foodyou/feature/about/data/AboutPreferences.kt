package com.maksimowiczm.foodyou.feature.about.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object AboutPreferences {
    val githubStarClicked = booleanPreferencesKey("github_star_clicked")
}
