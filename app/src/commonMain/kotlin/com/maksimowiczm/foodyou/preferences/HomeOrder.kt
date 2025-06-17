package com.maksimowiczm.foodyou.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

class HomeOrder(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<String, List<HomeCard>>(
        dataStore = dataStore,
        key = stringPreferencesKey("home_order_v2")
    ) {
    override fun String?.toValue(): List<HomeCard> = runCatching {
        this
            ?.split(",")
            ?.map { it.trim() }
            ?.map { HomeCard.entries[it.toInt()] }
            ?: HomeCard.defaultOrder
    }.getOrElse { HomeCard.defaultOrder }

    override fun List<HomeCard>.toStore(): String = joinToString(",") { it.ordinal.toString() }
}
