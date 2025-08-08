package com.maksimowiczm.foodyou.feature.shared.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.MeasurementType
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

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

        is Measurement.Gram -> {
            value.formatClipZeros() + " " + stringResource(Res.string.unit_gram_short)
        }

        is Measurement.Milliliter -> {
            value.formatClipZeros() + " " + stringResource(Res.string.unit_milliliter_short)
        }
    }

@Composable
fun MeasurementType.stringResource(): String =
    when (this) {
        MeasurementType.Gram -> stringResource(Res.string.unit_gram_short)
        MeasurementType.Milliliter -> stringResource(Res.string.unit_milliliter_short)
        MeasurementType.Package -> stringResource(Res.string.product_package)
        MeasurementType.Serving -> stringResource(Res.string.product_serving)
    }

val Measurement.Companion.NullableSaver: Saver<Measurement?, ArrayList<Any>>
    get() =
        Saver(
            save = {
                val id =
                    when (it) {
                        null -> -1
                        is Measurement.Gram -> 0
                        is Measurement.Serving -> 2
                        is Measurement.Package -> 1
                        is Measurement.Milliliter -> 3
                    }

                val value =
                    when (it) {
                        null -> 0f
                        is Measurement.Gram -> it.value
                        is Measurement.Serving -> it.quantity
                        is Measurement.Package -> it.quantity
                        is Measurement.Milliliter -> it.value
                    }

                arrayListOf(id, value)
            },
            restore = {
                val id = it[0] as Int
                val value = it[1] as Double

                when (id) {
                    -1 -> null
                    0 -> Measurement.Gram(value)
                    1 -> Measurement.Package(value)
                    2 -> Measurement.Serving(value)
                    3 -> Measurement.Milliliter(value)
                    else -> error("Invalid measurement id: $id")
                }
            },
        )

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
                    }

                val value =
                    when (it) {
                        is Measurement.Gram -> it.value
                        is Measurement.Serving -> it.quantity
                        is Measurement.Package -> it.quantity
                        is Measurement.Milliliter -> it.value
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
                    else -> error("Invalid measurement id: $id")
                }
            },
        )
