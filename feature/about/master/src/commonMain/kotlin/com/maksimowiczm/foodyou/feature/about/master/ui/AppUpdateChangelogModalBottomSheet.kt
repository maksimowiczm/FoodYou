package com.maksimowiczm.foodyou.feature.about.master.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.business.opensource.domain.config.AppConfig
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.Settings
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

/** Modal bottom sheet that will show the changelog of the app if the user has not seen it yet. */
@Composable
fun AppUpdateChangelogModalBottomSheet(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val settingsRepository: UserPreferencesRepository<Settings> =
        koinInject(named(Settings::class.qualifiedName!!))
    val appConfig: AppConfig = koinInject()

    val currentVersion = remember(appConfig) { appConfig.versionName }
    val settings = settingsRepository.observe().collectAsStateWithLifecycle(null).value

    if (settings != null && currentVersion != settings.lastRememberedVersion) {
        ChangelogModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    settingsRepository.update { copy(lastRememberedVersion = currentVersion) }
                }
            },
            modifier = modifier,
        )
    }
}
