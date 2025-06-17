package com.maksimowiczm.foodyou.ui.changelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.BuildConfig
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.preferences.LatestRememberedVersion

/**
 * Modal bottom sheet that will show the changelog of the app if the user has not seen it yet.
 */
@Composable
fun AppUpdateChangelogModalBottomSheet(
    modifier: Modifier = Modifier,
    lastRememberedVersion: LatestRememberedVersion = userPreference()
) {
    val coroutineScope = rememberCoroutineScope()
    val currentVersion = remember { BuildConfig.VERSION_NAME }
    val latestRememberedVersion = lastRememberedVersion.observe()
        .collectAsStateWithLifecycle(lastRememberedVersion.getBlocking())
        .value

    if (latestRememberedVersion != currentVersion) {
        ChangelogModalBottomSheet(
            onDismissRequest = coroutineScope.lambda {
                lastRememberedVersion.set(currentVersion)
            },
            modifier = modifier
        )
    }
}
