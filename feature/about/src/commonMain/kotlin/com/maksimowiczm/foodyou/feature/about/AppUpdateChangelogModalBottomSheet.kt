package com.maksimowiczm.foodyou.feature.about

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.about.domain.Changelog
import com.maksimowiczm.foodyou.feature.about.preferences.LatestRememberedVersion
import com.maksimowiczm.foodyou.feature.about.ui.ChangelogModalBottomSheet
import kotlinx.coroutines.launch

/**
 * Modal bottom sheet that will show the changelog of the app if the user has not seen it yet.
 */
@Composable
fun AppUpdateChangelogModalBottomSheet(modifier: Modifier = Modifier) {
    val lastRememberedVersion = userPreference<LatestRememberedVersion>()
    val currentVersion = remember { Changelog.currentVersion?.version }
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
