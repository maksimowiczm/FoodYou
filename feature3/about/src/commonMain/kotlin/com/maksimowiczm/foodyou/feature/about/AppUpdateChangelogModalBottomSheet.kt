package com.maksimowiczm.foodyou.feature.about

import FoodYou.feature3.about.BuildConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.about.preferences.LatestRememberedVersion
import com.maksimowiczm.foodyou.feature.about.ui.ChangelogModalBottomSheet
import kotlinx.coroutines.launch

/**
 * Modal bottom sheet that will show the changelog of the app if the user has not seen it yet.
 */
@Composable
fun AppUpdateChangelogModalBottomSheet(modifier: Modifier = Modifier) {
    val lastRememberedVersion = userPreference<LatestRememberedVersion>()
    val currentVersion = remember { BuildConfig.VERSION_NAME }
    val coroutineScope = rememberCoroutineScope()
    val latestRememberedVersion by lastRememberedVersion.collectAsStateWithLifecycle(null)

    if (latestRememberedVersion != currentVersion) {
        ChangelogModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    lastRememberedVersion.set(currentVersion)
                }
            },
            modifier = modifier
        )
    }
}
