package com.maksimowiczm.foodyou.app.ui.common.saveable

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

/**
 * Remember a mutable state that is persisted to DataStore. Similar to rememberSaveable but uses
 * DataStore for disk persistence.
 *
 * It's useful for small pieces of data that need to be persisted across app launches without
 * bloating the view model or repository.
 */
@Composable
fun <T> rememberDataStore(
    vararg inputs: Any?,
    key: Preferences.Key<T>,
    init: () -> MutableState<T>,
): MutableState<T> {
    val dataStore: DataStore<Preferences> = koinInject()
    val scope = rememberCoroutineScope()

    val state = remember(*inputs) { init() }

    LaunchedEffect(key, *inputs) {
        dataStore.data
            .map { prefs -> prefs[key] }
            .first()
            ?.let { persistedValue -> state.value = persistedValue }
    }

    return remember(key, *inputs) {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(newValue) {
                    state.value = newValue
                    scope.launch { dataStore.edit { prefs -> prefs[key] = newValue } }
                }

            override fun component1() = value

            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

/**
 * Remember a mutable state that is persisted to DataStore. Similar to rememberSaveable but uses
 * DataStore for disk persistence.
 *
 * Blocks on first composition to load the persisted value. If no value exists, uses the init lambda
 * to create the initial state.
 *
 * It's useful for small pieces of data that need to be persisted across app launches without
 * bloating the view model or repository.
 */
@Composable
fun <T> rememberBlockingDataStore(
    vararg inputs: Any?,
    key: Preferences.Key<T>,
    init: () -> MutableState<T>,
): MutableState<T> {
    val dataStore: DataStore<Preferences> = koinInject()
    val scope = rememberCoroutineScope()

    // Block and load the persisted value first, or use init if null
    val state: MutableState<T> =
        remember(*inputs) {
            runBlocking {
                val persistedValue = dataStore.data.map { prefs -> prefs[key] }.first()

                if (persistedValue != null) mutableStateOf(persistedValue) else init()
            }
        }

    return remember(key, *inputs) {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(newValue) {
                    state.value = newValue
                    scope.launch { dataStore.edit { prefs -> prefs[key] = newValue } }
                }

            override fun component1() = value

            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}
