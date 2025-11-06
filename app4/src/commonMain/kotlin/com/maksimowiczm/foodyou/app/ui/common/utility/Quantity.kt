package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.FluidOunces
import com.maksimowiczm.foodyou.common.domain.Grams
import com.maksimowiczm.foodyou.common.domain.Milliliters
import com.maksimowiczm.foodyou.common.domain.Ounces
import com.maksimowiczm.foodyou.common.domain.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.common.domain.ServingQuantity
import com.maksimowiczm.foodyou.food.search.domain.QuantityCalculator
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun Quantity.stringResource(
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
): String? {
    return when (this) {
        is AbsoluteQuantity.Volume -> this.stringResource()
        is AbsoluteQuantity.Weight -> this.stringResource()
        is PackageQuantity -> {
            if (packageQuantity == null) {
                return null
            }

            val packageQuantityString =
                stringResource(
                    Res.string.x_times_y,
                    this.packages.formatClipZeros(),
                    stringResource(Res.string.product_package),
                )

            val absoluteQuantityString =
                QuantityCalculator.calculateAbsoluteQuantity(packageQuantity, this).stringResource()

            remember(packageQuantityString, absoluteQuantityString) {
                "$packageQuantityString (${absoluteQuantityString})"
            }
        }

        is ServingQuantity -> {
            if (servingQuantity == null) {
                return null
            }

            val servingQuantityString =
                stringResource(
                    Res.string.x_times_y,
                    this.servings.formatClipZeros(),
                    stringResource(Res.string.product_serving),
                )

            val absoluteQuantityString =
                QuantityCalculator.calculateAbsoluteQuantity(servingQuantity, this).stringResource()

            remember(servingQuantityString, absoluteQuantityString) {
                "$servingQuantityString (${absoluteQuantityString})"
            }
        }
    }
}

@Composable
fun AbsoluteQuantity.stringResource(): String {
    return when (this) {
        is AbsoluteQuantity.Volume -> this.stringResource()
        is AbsoluteQuantity.Weight -> this.stringResource()
    }
}

@Composable
fun AbsoluteQuantity.Volume.stringResource(): String {
    return when (this.volume) {
        is FluidOunces ->
            this.volume.fluidOunces.formatClipZeros() +
                " " +
                stringResource(Res.string.unit_fluid_ounce_short)

        is Milliliters ->
            this.volume.milliliters.formatClipZeros() +
                " " +
                stringResource(Res.string.unit_milliliter_short)
    }
}

@Composable
fun AbsoluteQuantity.Weight.stringResource(): String {
    return when (this.weight) {
        is Grams ->
            this.weight.grams.formatClipZeros() + " " + stringResource(Res.string.unit_gram_short)

        is Ounces ->
            this.weight.ounces.formatClipZeros() + " " + stringResource(Res.string.unit_ounce_short)
    }
}
