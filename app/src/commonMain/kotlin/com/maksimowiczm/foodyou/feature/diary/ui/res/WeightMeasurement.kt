package com.maksimowiczm.foodyou.feature.diary.ui.res

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.product_package
import foodyou.app.generated.resources.product_serving
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.x_times_y
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeightMeasurement.stringResource() = when (this) {
    is WeightMeasurement.Package -> stringResource(
        Res.string.x_times_y,
        quantity.formatClipZeros(),
        stringResource(Res.string.product_package)
    )

    is WeightMeasurement.Serving -> stringResource(
        Res.string.x_times_y,
        quantity.formatClipZeros(),
        stringResource(Res.string.product_serving)
    )

    is WeightMeasurement.WeightUnit -> {
        weight.formatClipZeros() + " " + stringResource(Res.string.unit_gram_short)
    }
}
