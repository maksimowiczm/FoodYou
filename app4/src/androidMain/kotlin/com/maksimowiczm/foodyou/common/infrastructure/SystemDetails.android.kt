package com.maksimowiczm.foodyou.common.infrastructure

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

actual class SystemDetails(private val context: Context) : LifecycleEventObserver {
    val defaultLocale: Locale
        get() = context.defaultLocale

    private val languageTagFlow = MutableStateFlow(defaultLocale.toLanguageTag())

    actual val languageTag: Flow<String> = languageTagFlow.asStateFlow()

    actual fun setLanguage(tag: String) {
        val locale = LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(locale)
    }

    actual fun setSystemLanguage() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            languageTagFlow.value = defaultLocale.toLanguageTag()
        }
    }
}

actual fun Module.systemDetails(): KoinDefinition<out SystemDetails> = singleOf(::SystemDetails)
