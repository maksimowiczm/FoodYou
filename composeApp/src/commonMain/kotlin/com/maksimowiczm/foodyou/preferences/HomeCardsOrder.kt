package com.maksimowiczm.foodyou.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

enum class HomeCard {
    Calendar,
    Goals,
    Meals;

    companion object {
        val defaultOrder: List<HomeCard> = listOf(Calendar, Goals, Meals)
    }
}

class HomeCardsOrder(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<String, List<HomeCard>>(
        dataStore = dataStore,
        key = stringPreferencesKey("home_cards_order")
    ) {
    override fun String?.toValue(): List<HomeCard> {
        val order = this?.split(",")?.mapNotNull {
            HomeCard.entries.find { card -> card.ordinal.toString() == it }
        } ?: HomeCard.defaultOrder

        // Make sure that all cards are included in the order
        return order + HomeCard.defaultOrder.filterNot { it in order }
    }

    override fun List<HomeCard>.toStore(): String? =
        if (isEmpty()) null else joinToString(",") { it.ordinal.toString() }
}
