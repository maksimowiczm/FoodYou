package com.maksimowiczm.foodyou.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path
import org.koin.core.module.Module

const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

expect val dataStoreModule: Module

fun createDataStore(productFile: () -> Path): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = productFile
    )
