package com.maksimowiczm.foodyou.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object OpenFoodFactsPreferences {
    val isEnabled = booleanPreferencesKey("open_food_facts_enabled")
    val countryCode = stringPreferencesKey("open_food_facts_country_code")
    val hideSearchHint = booleanPreferencesKey("open_food_facts_hide_search_hint")
}
