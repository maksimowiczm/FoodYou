package com.maksimowiczm.foodyou.common.infrastructure.datastore

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences

internal operator fun <T> MutablePreferences.set(key: Preferences.Key<T>, value: T?) =
    when (val value = value) {
        null -> remove(key)
        else -> this[key] = value
    }
