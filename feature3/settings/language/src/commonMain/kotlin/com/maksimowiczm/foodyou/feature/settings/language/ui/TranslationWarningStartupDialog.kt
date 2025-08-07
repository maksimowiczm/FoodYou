package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.settings.language.presentation.LanguageViewModel
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import com.maksimowiczm.foodyou.feature.shared.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TranslationWarningStartupDialog(modifier: Modifier = Modifier) {
    val viewModel: LanguageViewModel = koinViewModel()
    val observeSettingsUseCase: ObserveSettingsUseCase = koinInject()
    val updateSettingsUseCase: UpdateSettingsUseCase = koinInject()

    val settings = observeSettingsUseCase.observe().collectAsStateWithLifecycle(null).value

    if (settings == null) return

    val translation by viewModel.translation.collectAsStateWithLifecycle()

    if (settings.showTranslationWarning && !translation.isVerified) {
        LanguageWarningDialog(
            onDismissRequest = {},
            onConfirm = {
                // Okay, that is a bit awkward
                viewModel.viewModelScope.launch {
                    updateSettingsUseCase.update(settings.copy(showTranslationWarning = false))
                }
            },
            modifier = modifier,
        )
    }
}
