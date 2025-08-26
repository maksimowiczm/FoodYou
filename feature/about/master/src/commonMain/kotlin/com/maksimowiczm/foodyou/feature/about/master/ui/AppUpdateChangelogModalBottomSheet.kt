package com.maksimowiczm.foodyou.feature.about.master.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.config.AppConfig
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/** Modal bottom sheet that will show the changelog of the app if the user has not seen it yet. */
@Composable
fun AppUpdateChangelogModalBottomSheet(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val observeSettingsUseCase: ObserveSettingsUseCase = koinInject()
    val commandBus: CommandBus = koinInject()
    val appConfig: AppConfig = koinInject()

    val currentVersion = remember(appConfig) { appConfig.versionName }
    val settings = observeSettingsUseCase.observe().collectAsStateWithLifecycle(null).value

    if (settings != null && currentVersion != settings.lastRememberedVersion) {
        ChangelogModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    commandBus.dispatch(
                        PartialSettingsUpdateCommand(lastRememberedVersion = currentVersion)
                    )
                }
            },
            modifier = modifier,
        )
    }
}
