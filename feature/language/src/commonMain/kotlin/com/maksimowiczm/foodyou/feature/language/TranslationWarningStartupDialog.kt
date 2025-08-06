package com.maksimowiczm.foodyou.feature.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.system.SystemDetails
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.system.isUS
import com.maksimowiczm.foodyou.feature.language.preferences.ShowTranslationWarning
import com.maksimowiczm.foodyou.feature.language.ui.LanguageWarningDialog
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TranslationWarningStartupDialog(
    modifier: Modifier = Modifier,
    systemDetails: SystemDetails = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()

    val showTranslationWarningPreference: ShowTranslationWarning = userPreference()
    val showTranslationWarning by showTranslationWarningPreference
        .collectAsStateWithLifecycle(false)

    if (showTranslationWarning && !systemDetails.isUS) {
        LanguageWarningDialog(
            onDismissRequest = {},
            onConfirm = {
                coroutineScope.launch {
                    showTranslationWarningPreference.set(false)
                }
            },
            modifier = modifier
        )
    }
}
