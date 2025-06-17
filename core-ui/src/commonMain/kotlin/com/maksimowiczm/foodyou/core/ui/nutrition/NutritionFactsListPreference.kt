package com.maksimowiczm.foodyou.core.ui.nutrition

import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.core.model.NutritionFactsField
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class NutritionFactsListPreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<String, NutritionFactsListPreferences>(
        dataStore = dataStore,
        key = stringPreferencesKey("nutrition_facts_list_preferences")
    ) {
    override fun String?.toValue(): NutritionFactsListPreferences = runCatching {
        this?.let {
            Json.Default.decodeFromString<NutritionFactsListPreferences>(it)
        } ?: NutritionFactsListPreferences()
    }.getOrDefault(NutritionFactsListPreferences())

    override fun NutritionFactsListPreferences.toStore(): String? = runCatching {
        Json.Default.encodeToString<NutritionFactsListPreferences>(this)
    }.getOrNull()

    suspend fun reset() {
        set(NutritionFactsListPreferences())
    }
}

@Immutable
@Serializable
data class NutritionFactsListPreferences(
    val order: List<NutritionFactsField> = NutritionFactsField.defaultOrder,
    val enabled: List<NutritionFactsField> = NutritionFactsField.entries
) {
    val orderedEnabled: List<NutritionFactsField>
        get() = order.filter { it in enabled }
}

val NutritionFactsField.Companion.defaultOrder: List<NutritionFactsField>
    get() = NutritionFactsField.entries
