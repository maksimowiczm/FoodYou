package com.maksimowiczm.foodyou.feature.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.shared.ui.res.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun Measurement.stringResourceWithWeight(
    totalWeight: Double?,
    servingWeight: Double?,
    isLiquid: Boolean,
): String? {
    val weight =
        when (this) {
            is Measurement.Gram,
            is Measurement.Milliliter -> null

            is Measurement.Package ->
                if (totalWeight == null) {
                    return null
                } else {
                    totalWeight * this.quantity
                }

            is Measurement.Serving ->
                if (servingWeight == null) {
                    return null
                } else {
                    servingWeight * this.quantity
                }
        }

    val measurementString = this.stringResource()
    val suffix =
        if (isLiquid) {
            stringResource(Res.string.unit_milliliter_short)
        } else {
            stringResource(Res.string.unit_gram_short)
        }

    return remember(measurementString, suffix, weight) {
        buildString {
            append(measurementString)
            if (weight != null) {
                append(" (${weight.formatClipZeros()} $suffix)")
            }
        }
    }
}
