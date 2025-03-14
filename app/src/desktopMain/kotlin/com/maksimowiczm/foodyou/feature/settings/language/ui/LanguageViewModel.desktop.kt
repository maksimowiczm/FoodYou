package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.data.SystemInfoRepository
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
        } else {
        }
    }
}
