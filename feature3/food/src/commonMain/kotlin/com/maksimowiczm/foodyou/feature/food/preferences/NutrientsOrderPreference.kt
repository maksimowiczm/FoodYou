package com.maksimowiczm.foodyou.feature.food.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
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
    Minerals;

    companion object {
        val defaultOrder: List<NutrientsOrder>
            get() = listOf(Proteins, Fats, Carbohydrates, Other, Vitamins, Minerals)
    }
}

class NutrientsOrderPreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<String, List<NutrientsOrder>>(
        dataStore = dataStore,
        key = stringPreferencesKey("food:nutrients_order")
    ) {
    override fun String?.toValue(): List<NutrientsOrder> = runCatching {
        this
            ?.split(",")
            ?.map {
                NutrientsOrder.entries[it.toInt()]
            }
    }.getOrNull() ?: NutrientsOrder.defaultOrder

    override fun List<NutrientsOrder>.toStore(): String? = runCatching {
        joinToString(",") { it.ordinal.toString() }
    }.getOrElse {
        null
    }

    suspend fun reset() = set(NutrientsOrder.defaultOrder)
}
