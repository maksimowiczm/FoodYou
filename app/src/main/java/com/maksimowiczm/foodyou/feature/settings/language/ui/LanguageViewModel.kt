package com.maksimowiczm.foodyou.feature.settings.language.ui

import androidx.lifecycle.ViewModel

abstract class LanguageViewModel : ViewModel() {
    abstract val tag: String
    abstract val languageName: String
    abstract fun onLanguageSelect(tag: String?)
    abstract fun onHelpTranslate()
}
