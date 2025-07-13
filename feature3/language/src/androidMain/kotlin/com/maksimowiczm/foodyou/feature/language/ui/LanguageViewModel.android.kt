package com.maksimowiczm.foodyou.feature.language.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.util.SystemDetails
import com.maksimowiczm.foodyou.feature.language.preferences.ShowTranslationWarning
import java.util.Locale
import kotlinx.coroutines.launch

internal actual class LanguageViewModel(
    private val androidSystemDetails: SystemDetails,
    dataStore: DataStore<Preferences>,
    private val showTranslationWarning: ShowTranslationWarning = dataStore.userPreference()
) : ViewModel() {
    private val locale: Locale
        get() = androidSystemDetails.defaultLocale

    val tag: String
        get() = locale.toLanguageTag()

    actual val languageName: String
        get() {
            val tag = locale.toLanguageTag()
            return Locale.forLanguageTag(tag).displayName
        }

    /**
     * Set the application locale based on the language tag.
     *
     * @param tag The language tag to set the application locale to. If null, the locale will be set
     * to the default locale.
     */
    fun onLanguageSelect(tag: String?) {
        viewModelScope.launch {
            showTranslationWarning.set(true)
        }

        if (tag == null) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        } else {
            val locale = LocaleListCompat.forLanguageTags(tag)
            AppCompatDelegate.setApplicationLocales(locale)
        }
    }
}
