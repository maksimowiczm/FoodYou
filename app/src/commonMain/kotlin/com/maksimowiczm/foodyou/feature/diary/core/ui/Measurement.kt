package com.maksimowiczm.foodyou.feature.diary.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.Measurement
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.product_package
import foodyou.app.generated.resources.product_serving
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.x_times_y
import org.jetbrains.compose.resources.stringResource

@Composable
fun Measurement.stringResource() = when (this) {
    is Measurement.Package -> stringResource(
        Res.string.x_times_y,
        quantity.formatClipZeros(),
        stringResource(Res.string.product_package)
    )

    is Measurement.Serving -> stringResource(
        Res.string.x_times_y,
        quantity.formatClipZeros(),
        stringResource(Res.string.product_serving)
    )

    is Measurement.Gram -> {
        value.formatClipZeros() + " " + stringResource(Res.string.unit_gram_short)
    }
}

val Measurement.Companion.Saver: Saver<Measurement?, ArrayList<Any>>
    get() = Saver(
        save = {
            val id = when (it) {
                null -> -1
                is Measurement.Gram -> 0
                is Measurement.Serving -> 2
                is Measurement.Package -> 1
            }

            val value = when (it) {
                null -> 0f
                is Measurement.Gram -> it.value
                is Measurement.Serving -> it.quantity
                is Measurement.Package -> it.quantity
            }

            arrayListOf<Any>(id, value)
        },
        restore = {
            val id = it[0] as Int
            val value = it[1] as Float

            when (id) {
                -1 -> null
                0 -> Measurement.Gram(value)
                1 -> Measurement.Package(value)
                2 -> Measurement.Serving(value)
                else -> error("Invalid measurement id: $id")
            }
        }
    )
