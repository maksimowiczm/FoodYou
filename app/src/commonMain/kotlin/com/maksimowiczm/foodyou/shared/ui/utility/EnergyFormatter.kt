package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.kilojoules
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

/** Utility object for formatting [Energy] and [EnergyUnit] into localized strings. */
object EnergyFormatter {
    /**
     * Formats this [Energy] into a localized string using its own [Energy.unit].
     *
     * @return Localized string representation of the energy value and its unit.
     */
    @Composable
    fun Energy.stringResource(): String {
        return when (unit) {
            EnergyUnit.Kilocalories ->
                kilocalories.roundToInt().toString() + " " + unit.stringResource()

            EnergyUnit.Kilojoules ->
                kilojoules.roundToInt().toString() + " " + unit.stringResource()
        }
    }

    /**
     * Returns the localized string representation of this [EnergyUnit].
     *
     * @return Localized unit string (e.g., "kcal" or "kJ").
     */
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
