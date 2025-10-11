package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.TypeConverter
import com.maksimowiczm.foodyou.account.domain.EnergyFormat

class EnergyFormatConverter {
    @TypeConverter
    fun toEnergyFormat(value: Int): EnergyFormat {
        return when (value) {
            0 -> EnergyFormat.Kilocalories
            1 -> EnergyFormat.Kilojoules
            else -> error("Unknown energy format value: $value")
        }
    }

    @TypeConverter
    fun fromEnergyFormat(energyFormat: EnergyFormat): Int {
        return when (energyFormat) {
            EnergyFormat.Kilocalories -> 0
            EnergyFormat.Kilojoules -> 1
        }
    }
}
