package com.maksimowiczm.foodyou.feature.food.data.network

/**
 * Helper function to convert a unit to a multiplier for a target unit.
 *
 * @param unit The original unit to convert from.
 * @param targetUnit The unit to convert to.
 *
 * @return The multiplier to convert the value from the original unit to the target unit.
 */
internal fun multiplierForUnit(unit: String?, targetUnit: String): Double = when (targetUnit) {
    "g" -> when (unit) {
        "g" -> 1.0
        "mg" -> 0.001
        "µg", "mcg" -> 0.000001
        else -> 1.0
    }

    "mg" -> when (unit) {
        "g" -> 1000.0
        "mg" -> 1.0
        "µg", "mcg" -> 0.001
        else -> 1.0
    }

    "mcg" -> when (unit) {
        "g" -> 1000000.0
        "mg" -> 1000.0
        "µg", "mcg" -> 1.0
        else -> 1.0
    }

    else -> 1.0
}
