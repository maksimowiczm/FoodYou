package com.maksimowiczm.foodyou.features.diary

import androidx.compose.runtime.*
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.shared.ui.saveable.rememberBlockingDataStore

private val isTrackedKey = booleanPreferencesKey("ui:AddDiaryIsTracked")

@Composable
fun rememberIsTracked() = rememberBlockingDataStore(key = isTrackedKey) { mutableStateOf(false) }
