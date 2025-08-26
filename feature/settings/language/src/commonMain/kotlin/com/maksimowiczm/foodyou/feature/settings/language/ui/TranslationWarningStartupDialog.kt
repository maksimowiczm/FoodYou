package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.settings.application.command.PartialSettingsUpdateCommand
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.feature.settings.language.presentation.LanguageViewModel
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TranslationWarningStartupDialog(modifier: Modifier = Modifier) {
    val viewModel: LanguageViewModel = koinViewModel()
    val observeSettingsUseCase: ObserveSettingsUseCase = koinInject()
    val commandBus: CommandBus = koinInject()

    val showTranslationWarning =
        observeSettingsUseCase
            .observe()
            .map { it.showTranslationWarning }
            .collectAsStateWithLifecycle(null)
            .value

    if (showTranslationWarning == null) return

    val translation by viewModel.translation.collectAsStateWithLifecycle()

    if (showTranslationWarning && !translation.isVerified) {
        LanguageWarningDialog(
            onDismissRequest = {},
            onConfirm = {
                // Okay, that is a bit awkward
                viewModel.viewModelScope.launch {
                    commandBus.dispatch(
                        PartialSettingsUpdateCommand(showTranslationWarning = false)
                    )
                }
            },
            modifier = modifier,
        )
    }
}
