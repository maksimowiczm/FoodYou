package com.maksimowiczm.foodyou.preferences.infrastructure

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.infrastructure.defaultLocale
import com.maksimowiczm.foodyou.preferences.domain.LanguagePreference
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

actual class LanguagePreferenceProvider(private val context: Context) :
    UserPreferenceProvider<LanguagePreference>, LifecycleEventObserver {
    actual override val key: String = LanguagePreference::class.qualifiedName!!

    actual override fun observe(): Flow<LanguagePreference> = defaultLocaleFlow.map {
        LanguagePreference(Language.fromTag(it.toLanguageTag()))
    }

    actual override suspend fun update(transform: (LanguagePreference) -> LanguagePreference) {
        val updated = transform(observe().first())

        val localList =
            when (updated.language) {
                null -> LocaleListCompat.getEmptyLocaleList()
                else -> LocaleListCompat.forLanguageTags(updated.language.tag)
            }

        AppCompatDelegate.setApplicationLocales(localList)
    }

    val defaultLocaleFlow: StateFlow<Locale>
        field = MutableStateFlow(context.defaultLocale)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            defaultLocaleFlow.value = context.defaultLocale
        }
    }
}

actual fun Module.languagePreferenceProvider(): KoinDefinition<out LanguagePreferenceProvider> =
    singleOf(::LanguagePreferenceProvider)
