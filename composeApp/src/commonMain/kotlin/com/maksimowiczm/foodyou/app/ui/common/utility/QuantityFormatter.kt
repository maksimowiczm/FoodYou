package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.app.ui.common.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.VolumeFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.WeightFormatter.stringResource
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityCalculator
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.food.toAbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.common.expect
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
     * - **Absolute quantities** (weight/volume): formatted directly with units
     * - **Package quantities**: formatted as "X × package (absolute equivalent)"
     * - **Serving quantities**: formatted as "X × serving (absolute equivalent)"
     *
     * @param packageQuantity The absolute quantity per package, required when formatting
     *   [PackageQuantity].
     * @param servingQuantity The absolute quantity per serving, required when formatting
     *   [ServingQuantity].
     * @return [Result.Success] with a localized string, or [Result.Error] when required context
     *   quantities are missing.
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
                        this.packages.formatCompact(),
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
                        this.servings.formatCompact(),
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

    @Composable
    fun ServingQuantity.stringResource(servingQuantity: AbsoluteQuantity): String =
        this.stringResource(packageQuantity = null, servingQuantity = servingQuantity)
            .expect("Serving quantity is calculated when serving quantity is provided.")

    @Composable
    fun PackageQuantity.stringResource(packageQuantity: AbsoluteQuantity): String =
        this.stringResource(packageQuantity = packageQuantity, servingQuantity = packageQuantity)
            .expect("Package quantity is calculated when package quantity is provided.")

    /**
     * Converts an [AbsoluteQuantity] to its localized string representation.
     *
     * Delegates to specific formatting functions based on the quantity type (volume or weight).
     *
     * @return Localized string with a formatted value and unit abbreviation.
     */
    @Composable
    fun AbsoluteQuantity.stringResource(): String {
        return when (this) {
            is AbsoluteQuantity.Volume -> this.volume.stringResource()
            is AbsoluteQuantity.Weight -> this.weight.stringResource()
        }
    }

    @Composable
    fun FoodComponentComponentQuantity.stringResource(): String {
        return when (this) {
            is FoodComponentComponentQuantity.Weight -> this.absoluteWeight.stringResource()

            is FoodComponentComponentQuantity.Package ->
                remember(this) { toQuantity() }
                    .stringResource(remember(packageWeight) { packageWeight.toAbsoluteQuantity() })

            is FoodComponentComponentQuantity.Serving ->
                remember(this) { toQuantity() }
                    .stringResource(remember(servingWeight) { servingWeight.toAbsoluteQuantity() })
        }
    }
}
