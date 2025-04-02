package com.maksimowiczm.foodyou.feature.diary.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object OpenFoodFactsPreferences {
    val isEnabled = booleanPreferencesKey("open_food_facts_enabled")

    /**
     * The country code used for searching in Open Food Facts.
     */
    val countryCode = stringPreferencesKey("open_food_facts_country_code")

    val hideSearchHint = booleanPreferencesKey("open_food_facts_hide_search_hint")
}
