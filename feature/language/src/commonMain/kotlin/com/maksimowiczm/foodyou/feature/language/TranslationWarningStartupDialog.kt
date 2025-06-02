package com.maksimowiczm.foodyou.feature.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.core.util.SystemDetails
import com.maksimowiczm.foodyou.core.util.isUS
import com.maksimowiczm.foodyou.feature.language.data.LanguagePreferences
import com.maksimowiczm.foodyou.feature.language.ui.LanguageWarningDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TranslationWarningStartupDialog(
    modifier: Modifier = Modifier,
    dataStore: DataStore<Preferences> = koinInject(),
    systemDetails: SystemDetails = koinInject()
) {
    var showTranslationWarning by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(dataStore) {
        dataStore
            .observe(LanguagePreferences.showTranslationWarning)
            .collectLatest { showTranslationWarning = it ?: true }
    }

    if (showTranslationWarning && !systemDetails.isUS) {
        LanguageWarningDialog(
            onDismissRequest = {},
            onConfirm = {
                coroutineScope.launch {
                    dataStore.set(LanguagePreferences.showTranslationWarning to false)
                }
            },
            modifier = modifier
        )
    }
}
