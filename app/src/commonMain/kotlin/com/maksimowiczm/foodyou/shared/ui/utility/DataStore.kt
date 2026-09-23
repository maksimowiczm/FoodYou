package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val LocalDataStore =
    staticCompositionLocalOf<DataStore<Preferences>> {
        object : DataStore<Preferences> {
            override val data: Flow<Preferences> = flowOf(emptyPreferences())

            override suspend fun updateData(
                transform: suspend (t: Preferences) -> Preferences
            ): Preferences = emptyPreferences()
        }
    }
