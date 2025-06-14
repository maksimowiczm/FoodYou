package com.maksimowiczm.foodyou.feature.measurement.ui.basic

import androidx.compose.runtime.Composable
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementEnum.Gram
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementEnum.Serving
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

enum class MeasurementEnum {
    Package,
    Serving,
    Gram,
    Milliliter;

    @Composable
    fun stringResource(): String = when (this) {
        Package -> stringResource(Res.string.product_package)
        Serving -> stringResource(Res.string.product_serving)
        Gram -> stringResource(Res.string.unit_gram_short)
        Milliliter -> stringResource(Res.string.unit_milliliter_short)
    }
}

fun Measurement.toEnum(): MeasurementEnum = when (this) {
    is Measurement.Gram -> Gram
    is Measurement.Milliliter -> MeasurementEnum.Milliliter
    is Measurement.Package -> MeasurementEnum.Package
    is Measurement.Serving -> Serving
}

val Measurement.value: Float
    get() = when (this) {
        is Measurement.Gram -> value
        is Measurement.Milliliter -> value
        is Measurement.Package -> quantity
        is Measurement.Serving -> quantity
    }
