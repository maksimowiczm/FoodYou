package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.maksimowiczm.foodyou.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.data.LinkHandler
import java.util.Locale

class AndroidLanguageViewModel(
    private val androidSystemInfoRepository: AndroidSystemInfoRepository,
    private val linkHandler: LinkHandler
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

    override fun onHelpTranslate() {
        linkHandler.openLink(CROWDIN_LINK)
    }

    private companion object {
        private const val CROWDIN_LINK = "https://crowdin.com/project/food-you"
    }
}
