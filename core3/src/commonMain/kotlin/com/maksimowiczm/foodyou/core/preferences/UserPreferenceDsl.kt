package com.maksimowiczm.foodyou.core.preferences

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import org.koin.compose.koinInject

@Composable
inline fun <reified T : BaseDataStoreUserPreference<*, *>> userPreference(): T {
    val dataStore = koinInject<DataStore<Preferences>>()
    return dataStore.userPreference<T>()
}

@Composable
fun <T> UserPreference<T>.collectAsStateWithLifecycle(
    initialValue: T,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
) = this.observe().collectAsStateWithLifecycle(
    initialValue = initialValue,
    lifecycleOwner = lifecycleOwner,
    minActiveState = minActiveState,
    context = context
)

@Composable
fun <T> UserPreference<T>.collectAsStateWithLifecycleInitialBlock(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
) = this.observe().collectAsStateWithLifecycle(
    initialValue = getBlocking(),
    lifecycleOwner = lifecycleOwner,
    minActiveState = minActiveState,
    context = context
)
