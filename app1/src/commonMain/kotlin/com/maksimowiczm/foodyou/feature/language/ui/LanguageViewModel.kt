package com.maksimowiczm.foodyou.feature.language.ui

import androidx.lifecycle.ViewModel

expect class LanguageViewModel : ViewModel {
    val tag: String
    val languageName: String
    fun onLanguageSelect(tag: String?)
}
