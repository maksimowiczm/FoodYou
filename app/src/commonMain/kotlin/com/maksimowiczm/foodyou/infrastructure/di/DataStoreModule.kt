package com.maksimowiczm.foodyou.infrastructure.di

import org.koin.core.module.Module

const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

expect val dataStoreModule: Module
