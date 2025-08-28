package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.settings.language.presentation.LanguageViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TranslationWarningStartupDialog(modifier: Modifier = Modifier) {
    val viewModel: LanguageViewModel = koinViewModel()

    val showTranslationWarning by viewModel.showTranslationWarning.collectAsStateWithLifecycle()
    val translation by viewModel.translation.collectAsStateWithLifecycle()

    if (showTranslationWarning && !translation.isVerified) {
        LanguageWarningDialog(
            onDismissRequest = {},
            onConfirm = viewModel::hideTranslationWarning,
            modifier = modifier,
        )
    }
}
