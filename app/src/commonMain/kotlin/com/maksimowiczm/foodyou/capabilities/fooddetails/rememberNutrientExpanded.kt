package com.maksimowiczm.foodyou.capabilities.fooddetails

import androidx.compose.runtime.*
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.shared.ui.saveable.rememberBlockingDataStore

private val nutrientExpandedKey = booleanPreferencesKey("FoodDetailsScreenExpanded")

@Composable
fun rememberNutrientExpanded() =
    rememberBlockingDataStore(key = nutrientExpandedKey) { mutableStateOf(true) }
