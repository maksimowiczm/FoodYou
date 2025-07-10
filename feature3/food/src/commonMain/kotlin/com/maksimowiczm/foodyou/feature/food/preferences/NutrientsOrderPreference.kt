package com.maksimowiczm.foodyou.feature.food.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

enum class NutrientsOrder {
    Proteins,
    Fats,
    Carbohydrates,
    Other,
    Vitamins,
    Minerals;

    companion object {
        val defaultOrder = listOf(Proteins, Fats, Carbohydrates, Other, Vitamins, Minerals)
    }
}

class NutrientsOrderPreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Set<String>, List<NutrientsOrder>>(
        dataStore = dataStore,
        key = stringSetPreferencesKey("food:nutrients_order")
    ) {
    override fun Set<String>?.toValue(): List<NutrientsOrder> = runCatching {
        this?.map { NutrientsOrder.valueOf(it) } ?: NutrientsOrder.defaultOrder
    }.getOrElse {
        NutrientsOrder.defaultOrder
    }

    override fun List<NutrientsOrder>.toStore(): Set<String>? = runCatching {
        this.map { it.name }.toSet()
    }.getOrElse {
        null
    }
}
