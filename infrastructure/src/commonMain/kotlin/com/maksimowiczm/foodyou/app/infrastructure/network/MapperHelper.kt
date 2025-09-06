package com.maksimowiczm.foodyou.app.infrastructure.network

import com.maksimowiczm.foodyou.app.infrastructure.network.UnitType.GRAMS
import com.maksimowiczm.foodyou.app.infrastructure.network.UnitType.MICROGRAMS
import com.maksimowiczm.foodyou.app.infrastructure.network.UnitType.MILLIGRAMS

/**
 * Helper function to convert a unit to a multiplier for a target unit.
 *
 * @param targetUnit The unit to convert to. It can be GRAMS, MILLIGRAMS, or MICROGRAMS.
 * @param from The original unit to convert from. Defaults to GRAMS.
 * @return The multiplier to convert the value from the original unit to the target unit.
 */
internal fun multiplier(targetUnit: UnitType, from: UnitType = GRAMS) =
    when (targetUnit) {
        GRAMS ->
            when (from) {
                GRAMS -> 1.0
                MILLIGRAMS -> 0.001
                MICROGRAMS -> 0.000001
            }

        MILLIGRAMS ->
            when (from) {
                GRAMS -> 1000.0
                MILLIGRAMS -> 1.0
                MICROGRAMS -> 0.001
            }

        MICROGRAMS ->
            when (from) {
                GRAMS -> 1000000.0
                MILLIGRAMS -> 1000.0
                MICROGRAMS -> 1.0
            }
    }

internal enum class UnitType {
    GRAMS,
    MILLIGRAMS,
    MICROGRAMS;

    companion object {
        fun fromString(unit: String): UnitType? =
            when (unit.lowercase()) {
                "g" -> GRAMS
                "mg" -> MILLIGRAMS
                "ug",
                "µg",
                "mcg" -> MICROGRAMS
                else -> null
            }
    }
}
