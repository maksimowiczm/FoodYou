package com.maksimowiczm.foodyou.feature.fooddiary.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

internal enum class MealsCardsLayout {
    Horizontal,
    Vertical;

    companion object {
        val Default = Vertical
    }
}

internal class MealsCardsLayoutPreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Int, MealsCardsLayout>(
        dataStore = dataStore,
        key = intPreferencesKey("fooddiary:meals_cards_layout")
    ) {
    override fun Int?.toValue() = when (this) {
        null -> MealsCardsLayout.Default
        0 -> MealsCardsLayout.Horizontal
        1 -> MealsCardsLayout.Vertical
        else -> error("Unknown layout value: $this")
    }

    override fun MealsCardsLayout.toStore() = when (this) {
        MealsCardsLayout.Horizontal -> 0
        MealsCardsLayout.Vertical -> 1
    }
}
