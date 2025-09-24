package com.maksimowiczm.foodyou.food.infrastructure.network

/**
 * Helper function to convert a unit to a multiplier for a target unit.
 *
 * @param targetUnit The unit to convert to. It can be GRAMS, MILLIGRAMS, or MICROGRAMS.
 * @param from The original unit to convert from. Defaults to GRAMS.
 * @return The multiplier to convert the value from the original unit to the target unit.
 */
internal fun multiplier(targetUnit: UnitType, from: UnitType = UnitType.GRAMS) =
    when (targetUnit) {
        UnitType.GRAMS ->
            when (from) {
                UnitType.GRAMS -> 1.0
                UnitType.MILLIGRAMS -> 0.001
                UnitType.MICROGRAMS -> 0.000001
            }

        UnitType.MILLIGRAMS ->
            when (from) {
                UnitType.GRAMS -> 1000.0
                UnitType.MILLIGRAMS -> 1.0
                UnitType.MICROGRAMS -> 0.001
            }

        UnitType.MICROGRAMS ->
            when (from) {
                UnitType.GRAMS -> 1000000.0
                UnitType.MILLIGRAMS -> 1000.0
                UnitType.MICROGRAMS -> 1.0
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
