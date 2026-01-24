package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
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

object QuantityFormatter {
    sealed interface QuantityConversionError {
        object MissingPackageQuantity : QuantityConversionError

        object MissingServingQuantity : QuantityConversionError
    }

    /**
     * Converts a [Quantity] to its localized string representation.
     *
     * This function handles different quantity types:
     * - **Absolute quantities** (weight/volume): Formatted directly with units
     * - **Package quantities**: Formatted as "X × package (absolute equivalent)"
     * - **Serving quantities**: Formatted as "X × serving (absolute equivalent)"
     *
     * @param packageQuantity The absolute quantity per package, required for [PackageQuantity]
     *   formatting
     * @param servingQuantity The absolute quantity per serving, required for [ServingQuantity]
     *   formatting
     * @return Localized string representation, or null if required context quantities are missing
     * @see AbsoluteQuantity.stringResource
     * @see QuantityCalculator.calculateAbsoluteQuantity
     */
    @Composable
    fun Quantity.stringResource(
        packageQuantity: AbsoluteQuantity?,
        servingQuantity: AbsoluteQuantity?,
    ): Result<String, QuantityConversionError> {
        return when (this) {
            is AbsoluteQuantity.Volume -> Ok(this.stringResource())
            is AbsoluteQuantity.Weight -> Ok(this.stringResource())
            is PackageQuantity -> {
                if (packageQuantity == null) {
                    return Err(QuantityConversionError.MissingPackageQuantity)
                }

                val packageQuantityString =
                    stringResource(
                        Res.string.x_times_y,
                        this.packages.formatClipZeros(),
                        stringResource(Res.string.product_package),
                    )

                val absoluteQuantityString =
                    QuantityCalculator.calculateAbsoluteQuantity(packageQuantity, this)
                        .stringResource()

                remember(packageQuantityString, absoluteQuantityString) {
                    Ok("$packageQuantityString (${absoluteQuantityString})")
                }
            }

            is ServingQuantity -> {
                if (servingQuantity == null) {
                    return Err(QuantityConversionError.MissingServingQuantity)
                }

                val servingQuantityString =
                    stringResource(
                        Res.string.x_times_y,
                        this.servings.formatClipZeros(),
                        stringResource(Res.string.product_serving),
                    )

                val absoluteQuantityString =
                    QuantityCalculator.calculateAbsoluteQuantity(servingQuantity, this)
                        .stringResource()

                remember(servingQuantityString, absoluteQuantityString) {
                    Ok("$servingQuantityString (${absoluteQuantityString})")
                }
            }
        }
    }

    /**
     * Converts an [AbsoluteQuantity] to its localized string representation.
     *
     * Delegates to specific formatting functions based on the quantity type (volume or weight).
     *
     * @return Localized string with formatted value and unit abbreviation
     */
    @Composable
    fun AbsoluteQuantity.stringResource(): String {
        return when (this) {
            is AbsoluteQuantity.Volume -> this.stringResource()
            is AbsoluteQuantity.Weight -> this.stringResource()
        }
    }

    /**
     * Converts a volume quantity to its localized string representation.
     *
     * Formats the volume value with trailing zeros removed and appends the appropriate unit:
     * - Milliliters (ml)
     * - Fluid ounces (fl oz)
     *
     * @return Localized string in format "value unit" (e.g., "250 ml", "8 fl oz")
     */
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

    /**
     * Converts a weight quantity to its localized string representation.
     *
     * Formats the weight value with trailing zeros removed and appends the appropriate unit:
     * - Grams (g)
     * - Ounces (oz)
     *
     * @return Localized string in format "value unit" (e.g., "100 g", "3.5 oz")
     */
    @Composable
    fun AbsoluteQuantity.Weight.stringResource(): String {
        return when (this.weight) {
            is Grams ->
                this.weight.grams.formatClipZeros() +
                    " " +
                    stringResource(Res.string.unit_gram_short)

            is Ounces ->
                this.weight.ounces.formatClipZeros() +
                    " " +
                    stringResource(Res.string.unit_ounce_short)
        }
    }
}
