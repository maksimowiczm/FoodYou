package com.maksimowiczm.foodyou.feature.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.ext.lambda
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.util.SystemDetails
import com.maksimowiczm.foodyou.core.util.isUS
import com.maksimowiczm.foodyou.feature.language.preferences.ShowTranslationWarning
import com.maksimowiczm.foodyou.feature.language.ui.LanguageWarningDialog
import org.koin.compose.koinInject

@Composable
fun TranslationWarningStartupDialog(
    modifier: Modifier = Modifier,
    showTranslationWarningPreference: ShowTranslationWarning = userPreference(),
    systemDetails: SystemDetails = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val showTranslationWarning by showTranslationWarningPreference
        .collectAsStateWithLifecycle(false)

    if (showTranslationWarning && !systemDetails.isUS) {
        LanguageWarningDialog(
            onDismissRequest = {},
            onConfirm = coroutineScope.lambda {
                showTranslationWarningPreference.set(false)
            },
            modifier = modifier
        )
    }
}
