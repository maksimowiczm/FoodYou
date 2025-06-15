package com.maksimowiczm.foodyou.ui.changelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.data.AppPreferences
import org.koin.compose.koinInject

/**
 * Modal bottom sheet that will show the changelog of the app if the user has not seen it yet.
 */
@Composable
fun AppUpdateChangelogModalBottomSheet(
    modifier: Modifier = Modifier,
    dataStore: DataStore<Preferences> = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val latestRememberedVersion by dataStore.observeLatestRememberedVersionAsState()
    val currentVersion = remember { BuildConfig.VERSION_NAME }

    if (latestRememberedVersion != currentVersion) {
        ChangelogModalBottomSheet(
            onDismissRequest = coroutineScope.lambda {
                dataStore.set(AppPreferences.latestRememberedVersion to currentVersion)
            },
            modifier = modifier
        )
    }
}

@Composable
private fun DataStore<Preferences>.observeLatestRememberedVersionAsState() =
    observe(AppPreferences.latestRememberedVersion)
        .collectAsStateWithLifecycle(getBlocking(AppPreferences.latestRememberedVersion))
