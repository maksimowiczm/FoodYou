package com.maksimowiczm.foodyou.device.infrastructure

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.maksimowiczm.foodyou.common.infrastructure.datastore.set
import com.maksimowiczm.foodyou.device.domain.NutrientsColors

internal fun MutablePreferences.applyNutrientsColors(
    nutrientsColors: NutrientsColors
): MutablePreferences = apply {
    this[NutrientsColorsKeys.proteinsColor] = nutrientsColors.proteins?.toLong()
    this[NutrientsColorsKeys.carbohydratesColor] = nutrientsColors.carbohydrates?.toLong()
    this[NutrientsColorsKeys.fatsColor] = nutrientsColors.fats?.toLong()
}

internal fun Preferences.toNutrientsColors(): NutrientsColors =
    NutrientsColors(
        proteins = this[NutrientsColorsKeys.proteinsColor]?.toULong(),
        carbohydrates = this[NutrientsColorsKeys.carbohydratesColor]?.toULong(),
        fats = this[NutrientsColorsKeys.fatsColor]?.toULong(),
    )

private object NutrientsColorsKeys {
    val proteinsColor = longPreferencesKey("nutrientsColors:proteins")
    val carbohydratesColor = longPreferencesKey("nutrientsColors:carbohydrates")
    val fatsColor = longPreferencesKey("nutrientsColors:fats")
}
