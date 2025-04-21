package com.maksimowiczm.foodyou.feature.changelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ext.get
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.feature.changelog.data.ChangelogPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    val latestRememberedVersion by dataStore
        .observe(ChangelogPreferences.latestRememberedVersion)
        .collectAsStateWithLifecycle(
            runBlocking {
                dataStore.get(ChangelogPreferences.latestRememberedVersion)
            }
        )

    val currentVersion = BuildConfig.VERSION_NAME

    if (latestRememberedVersion != currentVersion) {
        ChangelogModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    dataStore.set(ChangelogPreferences.latestRememberedVersion to currentVersion)
                }
            },
            modifier = modifier
        )
    }
}
