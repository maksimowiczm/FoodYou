package com.maksimowiczm.foodyou.feature.settings.laguage

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.maksimowiczm.foodyou.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.feature.settings.language.ui.LanguageViewModel
import java.util.Locale

class AndroidLanguageViewModel(
    private val androidSystemInfoRepository: AndroidSystemInfoRepository
) : LanguageViewModel() {
    private val locale: Locale
        get() = androidSystemInfoRepository.defaultLocale

    override val tag: String
        get() = locale.toLanguageTag()

    override val languageName: String
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
    override fun onLanguageSelect(tag: String?) {
        if (tag == null) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
        } else {
            val locale = LocaleListCompat.forLanguageTags(tag)
            AppCompatDelegate.setApplicationLocales(locale)
        }
    }
}
