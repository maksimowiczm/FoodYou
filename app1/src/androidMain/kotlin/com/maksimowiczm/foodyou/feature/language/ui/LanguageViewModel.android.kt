package com.maksimowiczm.foodyou.feature.language.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import java.util.Locale

actual class LanguageViewModel(private val androidSystemInfoRepository: SystemInfoRepository) :
    ViewModel() {
    private val locale: Locale
        get() = androidSystemInfoRepository.defaultLocale

    actual val tag: String
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
    actual fun onLanguageSelect(tag: String?) {
        if (tag == null) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        } else {
            val locale = LocaleListCompat.forLanguageTags(tag)
            AppCompatDelegate.setApplicationLocales(locale)
        }
    }
}
