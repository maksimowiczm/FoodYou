package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.common.domain.EnergyUnit

internal class EnergyUnitConverter {
    @TypeConverter
    fun toEnergyUnit(value: Int): EnergyUnit {
        return when (value) {
            0 -> EnergyUnit.Kilocalories
            1 -> EnergyUnit.Kilojoules
            else -> error("Unknown energy format value: $value")
        }
    }

    @TypeConverter
    fun fromEnergyUnit(energyUnit: EnergyUnit): Int {
        return when (energyUnit) {
            EnergyUnit.Kilocalories -> 0
            EnergyUnit.Kilojoules -> 1
        }
    }
}
