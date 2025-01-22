package com.maksimowiczm.foodyou.feature.product.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object ProductPreferences {
    val openFoodFactsEnabled = booleanPreferencesKey("open_food_facts_enabled")
    val openFoodCountryCode = stringPreferencesKey("open_food_country_code")
}
