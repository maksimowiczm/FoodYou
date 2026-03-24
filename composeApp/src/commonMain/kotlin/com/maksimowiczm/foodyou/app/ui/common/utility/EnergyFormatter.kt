package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.kilojoules
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

object EnergyFormatter {
    @Composable
    fun Energy.stringResource(): String {
        return when (unit) {
            EnergyUnit.Kilocalories ->
                kilocalories.roundToInt().toString() + " " + unit.stringResource()

            EnergyUnit.Kilojoules ->
                kilojoules.roundToInt().toString() + " " + unit.stringResource()
        }
    }

    @Composable
    fun EnergyUnit.stringResource(): String {
        return when (this) {
            EnergyUnit.Kilocalories -> stringResource(Res.string.unit_kcal)
            EnergyUnit.Kilojoules -> stringResource(Res.string.unit_kilojoules)
        }
    }
}

/** CompositionLocal for [EnergyUnit]. It represents energy unit which is preferred by user. */
val LocalEnergyUnit = staticCompositionLocalOf { EnergyUnit.Kilocalories }

@Composable
fun EnergyUnitProvider(energyUnit: EnergyUnit, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalEnergyUnit provides energyUnit) { content() }
}
