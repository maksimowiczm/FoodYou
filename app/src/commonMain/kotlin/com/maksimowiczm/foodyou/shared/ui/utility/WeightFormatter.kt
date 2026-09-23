package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.micrograms
import com.maksimowiczm.foodyou.common.domain.milligrams
import com.maksimowiczm.foodyou.common.domain.ounces
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

object WeightFormatter {

    /**
     * Converts a weight quantity to its localized string representation.
     *
     * Formats the weight value with trailing zeros removed and appends the appropriate unit:
     * - Micrograms (mcg)
     * - Milligrams (mg)
     * - Grams (g)
     * - Ounces (oz)
     *
     * @return Localized string in the format "value unit" (e.g., "100 g", "3.5 oz").
     */
    @Composable
    fun Weight.stringResource(): String {
        return when (unit) {
            WeightUnit.Micrograms ->
                micrograms.formatCompact() + " " + stringResource(Res.string.unit_microgram_short)

            WeightUnit.Milligrams ->
                milligrams.formatCompact() + " " + stringResource(Res.string.unit_milligram_short)

            WeightUnit.Grams ->
                grams.formatCompact() + " " + stringResource(Res.string.unit_gram_short)

            WeightUnit.Ounces ->
                ounces.formatCompact() + " " + stringResource(Res.string.unit_ounce_short)
        }
    }
}
