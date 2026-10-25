package com.maksimowiczm.foodyou.preferences.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.preferences.domain.LanguagePreference
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

// TODO:
//  Ensure the persisted language preference is applied during application startup.
//  Locale.setDefault() alone may not update existing UI resources.
//  Revisit the locale-management approach to ensure consistent behavior across platforms.
actual class LanguagePreferenceProvider(private val dataStore: DataStore<Preferences>) :
    UserPreferenceProvider<LanguagePreference> {

    actual override val key: String = LanguagePreference::class.qualifiedName!!

    private val systemLocale: Locale = Locale.getDefault()

    private val languageTag: Flow<String> =
        dataStore.data.map { it[selectedLanguageKey] ?: systemLocale.toLanguageTag() }

    actual override fun observe(): Flow<LanguagePreference> = languageTag.map { tag ->
        LanguagePreference(Language.fromTag(tag))
    }

    actual override suspend fun update(transform: (LanguagePreference) -> LanguagePreference) {
        val updatedPreferences = dataStore.edit { preferences ->
            val currentTag = preferences[selectedLanguageKey] ?: systemLocale.toLanguageTag()
            val current = LanguagePreference(Language.fromTag(currentTag))
            val updated = transform(current)

            if (updated.language == null) {
                preferences.remove(selectedLanguageKey)
            } else {
                preferences[selectedLanguageKey] = updated.language.tag
            }
        }

        applyLocale(updatedPreferences[selectedLanguageKey])
    }

    private fun applyLocale(tag: String?) {
        Locale.setDefault(tag?.let { Locale.forLanguageTag(it) } ?: systemLocale)
    }

    private companion object {
        private val selectedLanguageKey = stringPreferencesKey("selected_language")
    }
}

actual fun Module.languagePreferenceProvider(): KoinDefinition<out LanguagePreferenceProvider> =
    singleOf(::LanguagePreferenceProvider)
