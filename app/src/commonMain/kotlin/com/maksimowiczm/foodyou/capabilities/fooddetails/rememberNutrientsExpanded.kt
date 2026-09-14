package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.runtime.*
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.shared.ui.saveable.rememberBlockingDataStore

private val nutrientsExpandedKey = booleanPreferencesKey("ui:NutrientsExpanded")

@Composable
fun rememberNutrientsExpanded() =
    rememberBlockingDataStore(key = nutrientsExpandedKey) { mutableStateOf(true) }
