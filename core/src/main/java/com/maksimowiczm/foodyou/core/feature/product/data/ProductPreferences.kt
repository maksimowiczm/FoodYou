package com.maksimowiczm.foodyou.core.feature.product.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object ProductPreferences {
    val openFoodFactsEnabled = booleanPreferencesKey("open_food_facts_enabled")
    val openFoodFactsCountryCode = stringPreferencesKey("open_food_facts_country_code")
}
