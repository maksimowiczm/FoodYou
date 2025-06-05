package com.maksimowiczm.foodyou.feature.product.data.network.usda

import androidx.datastore.preferences.core.stringPreferencesKey

internal object USDAPreferences {
    val apiKeyPreferenceKey = stringPreferencesKey("usda_api_key")
}
