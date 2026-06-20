package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.grams

/** Domain service for managing and transforming [FoodComponentComponentQuantity] instances. */
object FoodComponentQuantityUpdateService {

    /**
     * Maps a general [Quantity] to a component-specific [FoodComponentComponentQuantity],
     * incorporating the provided serving and package weight metadata.
     */
    fun map(
        quantity: Quantity,
        servingWeight: Weight?,
        packageWeight: Weight?,
    ): FoodComponentComponentQuantity =
        when (quantity) {
            is AbsoluteQuantity.Weight ->
                FoodComponentComponentQuantity.Weight(
                    absoluteWeight = quantity.weight,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )

            is AbsoluteQuantity.Volume ->
                FoodComponentComponentQuantity.Weight(
                    absoluteWeight = quantity.volume.milliliters.grams,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )

            is PackageQuantity -> {
                requireNotNull(packageWeight) { "Package quantity requires package weight" }
                FoodComponentComponentQuantity.Package(
                    packages = quantity.packages,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )
            }

            is ServingQuantity -> {
                requireNotNull(servingWeight) { "Serving quantity requires serving weight" }
                FoodComponentComponentQuantity.Serving(
                    servings = quantity.servings,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )
            }
        }

    /**
     * Updates an existing [FoodComponentComponentQuantity] with new serving and package weights.
     *
     * If a quantity variant (e.g., [FoodComponentComponentQuantity.Serving]) requires a weight that
     * becomes `null`, it is converted to a direct [FoodComponentComponentQuantity.Weight] using the
     * [FoodComponentComponentQuantity.absoluteWeight].
     */
    fun update(
        current: FoodComponentComponentQuantity,
        servingWeight: Weight?,
        packageWeight: Weight?,
    ): FoodComponentComponentQuantity =
        when (current) {
            is FoodComponentComponentQuantity.Serving ->
                if (servingWeight != null)
                    current.copy(servingWeight = servingWeight, packageWeight = packageWeight)
                else
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = current.absoluteWeight,
                        servingWeight = servingWeight,
                        packageWeight = packageWeight,
                    )

            is FoodComponentComponentQuantity.Package ->
                if (packageWeight != null)
                    current.copy(packageWeight = packageWeight, servingWeight = servingWeight)
                else
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = current.absoluteWeight,
                        servingWeight = servingWeight,
                        packageWeight = packageWeight,
                    )

            is FoodComponentComponentQuantity.Weight ->
                current.copy(
                    absoluteWeight = current.absoluteWeight,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )
        }
}
