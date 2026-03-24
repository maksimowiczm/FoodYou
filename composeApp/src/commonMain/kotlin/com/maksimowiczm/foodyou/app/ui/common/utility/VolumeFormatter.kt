package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.Volume
import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

object VolumeFormatter {
    /**
     * Converts a volume quantity to its localized string representation.
     *
     * Formats the volume value with trailing zeros removed and appends the appropriate unit:
     * - Milliliters (ml)
     * - Fluid ounces (fl oz)
     *
     * @return Localized string in the format "value unit" (e.g., "250 ml", "8 fl oz").
     */
    @Composable
    fun Volume.stringResource(): String {
        return when (unit) {
            VolumeUnit.Milliliters ->
                milliliters.formatClipZeros() +
                    " " +
                    stringResource(Res.string.unit_milliliter_short)

            VolumeUnit.FluidOunces ->
                fluidOunces.formatClipZeros() +
                    " " +
                    stringResource(Res.string.unit_fluid_ounce_short)
        }
    }
}
