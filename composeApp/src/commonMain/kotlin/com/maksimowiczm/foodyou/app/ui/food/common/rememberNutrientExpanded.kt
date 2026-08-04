package com.maksimowiczm.foodyou.app.ui.food.common

import androidx.compose.runtime.*
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.app.ui.common.saveable.rememberBlockingDataStore

private val nutrientExpandedKey = booleanPreferencesKey("FoodDetailsScreenExpanded")

@Composable
internal fun rememberNutrientExpanded() =
    rememberBlockingDataStore(key = nutrientExpandedKey) { mutableStateOf(true) }
