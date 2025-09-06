package com.maksimowiczm.foodyou.feature.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import com.maksimowiczm.foodyou.shared.measurement.Measurement
import com.maksimowiczm.foodyou.shared.measurement.MeasurementType
import com.maksimowiczm.foodyou.shared.measurement.type
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
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
            is Measurement.ImmutableMeasurement -> null

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

@Composable
fun Measurement.stringResource() =
    when (this) {
        is Measurement.Package ->
            stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_package),
            )

        is Measurement.Serving ->
            stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_serving),
            )

        is Measurement.ImmutableMeasurement ->
            value.formatClipZeros() + " " + this.type.stringResource()
    }

@Composable
fun MeasurementType.stringResource(): String =
    when (this) {
        MeasurementType.Gram -> stringResource(Res.string.unit_gram_short)
        MeasurementType.Milliliter -> stringResource(Res.string.unit_milliliter_short)
        MeasurementType.Package -> stringResource(Res.string.product_package)
        MeasurementType.Serving -> stringResource(Res.string.product_serving)
        MeasurementType.Ounce -> stringResource(Res.string.unit_ounce_short)
        MeasurementType.FluidOunce -> stringResource(Res.string.unit_fluid_ounce_short)
    }

val Measurement.Companion.Saver: Saver<Measurement, ArrayList<Any>>
    get() =
        Saver(
            save = {
                val id =
                    when (it) {
                        is Measurement.Gram -> 0
                        is Measurement.Serving -> 2
                        is Measurement.Package -> 1
                        is Measurement.Milliliter -> 3
                        is Measurement.Ounce -> 4
                        is Measurement.FluidOunce -> 5
                    }

                val value =
                    when (it) {
                        is Measurement.Gram -> it.value
                        is Measurement.Serving -> it.quantity
                        is Measurement.Package -> it.quantity
                        is Measurement.Milliliter -> it.value
                        is Measurement.Ounce -> it.value
                        is Measurement.FluidOunce -> it.value
                    }

                arrayListOf(id, value)
            },
            restore = {
                val id = it[0] as Int
                val value = it[1] as Double

                when (id) {
                    0 -> Measurement.Gram(value)
                    1 -> Measurement.Package(value)
                    2 -> Measurement.Serving(value)
                    3 -> Measurement.Milliliter(value)
                    4 -> Measurement.Ounce(value)
                    5 -> Measurement.FluidOunce(value)
                    else -> error("Invalid measurement id: $id")
                }
            },
        )
