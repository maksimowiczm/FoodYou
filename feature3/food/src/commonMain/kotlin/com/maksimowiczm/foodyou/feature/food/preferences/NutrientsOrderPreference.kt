package com.maksimowiczm.foodyou.feature.food.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder.Carbohydrates
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder.Fats
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder.Minerals
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder.Other
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder.Proteins
import com.maksimowiczm.foodyou.feature.food.preferences.NutrientsOrder.Vitamins

enum class NutrientsOrder {
    Proteins,
    Fats,
    Carbohydrates,
    Other,
    Vitamins,
    Minerals
}

class NutrientsOrderPreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Set<String>, List<NutrientsOrder>>(
        dataStore = dataStore,
        key = stringSetPreferencesKey("food:nutrients_order")
    ) {
    override fun Set<String>?.toValue(): List<NutrientsOrder> = runCatching {
        this?.map { NutrientsOrder.valueOf(it) } ?: defaultOrder
    }.getOrElse {
        defaultOrder
    }

    override fun List<NutrientsOrder>.toStore(): Set<String>? = runCatching {
        this.map { it.name }.toSet()
    }.getOrElse {
        null
    }

    val defaultOrder: List<NutrientsOrder>
        get() = listOf(Proteins, Fats, Carbohydrates, Other, Vitamins, Minerals)
}
