package com.maksimowiczm.foodyou.core.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.x_times_y
import org.jetbrains.compose.resources.stringResource

sealed interface Measurement {

    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    fun weight(food: Food): Float? = when (this) {
        is Gram -> value
        is Package -> food.totalWeight?.let { weight(it) }
        is Serving -> food.servingWeight?.let { weight(it) }
    }

    @JvmInline
    value class Gram(val value: Float) : Measurement

    @JvmInline
    value class Package(val quantity: Float) : Measurement {
        fun weight(packageWeight: Float) = packageWeight * quantity
    }

    @JvmInline
    value class Serving(val quantity: Float) : Measurement {
        fun weight(servingWeight: Float) = servingWeight * quantity
    }

    companion object {
        fun defaultForFood(food: Food): Measurement = when {
            food.servingWeight != null -> Serving(1f)
            food.totalWeight != null -> Package(1f)
            else -> Gram(100f)
        }
    }
}

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
