package com.maksimowiczm.foodyou.data

import androidx.datastore.preferences.core.stringPreferencesKey

object HomePreferences {
    // Rename calories card to goals card, update preference version
    val homeOrder = stringPreferencesKey("home_order_v2")
}
