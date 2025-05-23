package com.maksimowiczm.foodyou.core.ext

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

fun <T> DataStore<Preferences>.observe(key: Preferences.Key<T>) = data.map { preferences ->
    preferences[key]
}

suspend fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? = observe(key).first()

fun <T> DataStore<Preferences>.getBlocking(key: Preferences.Key<T>): T? =
    runBlocking { observe(key).first() }

suspend fun DataStore<Preferences>.set(vararg pairs: Preferences.Pair<*>) {
    edit { preferences ->
        preferences.putAll(*pairs)
    }
}

suspend fun DataStore<Preferences>.setNull(key: Preferences.Key<*>) {
    edit { preferences ->
        preferences.remove(key)
    }
}
