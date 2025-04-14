package com.maksimowiczm.foodyou.feature.language.data

import androidx.datastore.preferences.core.booleanPreferencesKey

internal object LanguagePreferences {
    val showTranslationWarning = booleanPreferencesKey("show_translation_warning")
}
