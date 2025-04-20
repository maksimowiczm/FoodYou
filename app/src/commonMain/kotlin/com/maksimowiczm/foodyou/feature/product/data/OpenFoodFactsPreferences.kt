package com.maksimowiczm.foodyou.feature.product.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object OpenFoodFactsPreferences {
    val hideExternalBrowserWarning = booleanPreferencesKey("hide_external_browser_warning")
}
