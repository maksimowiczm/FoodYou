package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.grams

/** Domain service for managing and transforming [FoodSnapshotQuantity] instances. */
object FoodSnapshotQuantityUpdateService {

    /**
     * Maps a general [Quantity] to a component-specific [FoodSnapshotQuantity], incorporating the
     * provided serving and package weight metadata.
     */
    fun map(
        quantity: Quantity,
        servingWeight: Weight?,
        packageWeight: Weight?,
    ): FoodSnapshotQuantity =
        when (quantity) {
            is AbsoluteQuantity.Weight ->
                FoodSnapshotQuantity.Weight(
                    absoluteWeight = quantity.weight,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )

            is AbsoluteQuantity.Volume ->
                FoodSnapshotQuantity.Weight(
                    absoluteWeight = quantity.volume.milliliters.grams,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )

            is PackageQuantity -> {
                requireNotNull(packageWeight) { "Package quantity requires package weight" }
                FoodSnapshotQuantity.Package(
                    packages = quantity.packages,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )
            }

            is ServingQuantity -> {
                requireNotNull(servingWeight) { "Serving quantity requires serving weight" }
                FoodSnapshotQuantity.Serving(
                    servings = quantity.servings,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )
            }
        }

    /**
     * Updates an existing [FoodSnapshotQuantity] with new serving and package weights.
     *
     * If a quantity variant (e.g., [FoodSnapshotQuantity.Serving]) requires a weight that becomes
     * `null`, it is converted to a direct [FoodSnapshotQuantity.Weight] using the
     * [FoodSnapshotQuantity.absoluteWeight].
     */
    fun update(
        current: FoodSnapshotQuantity,
        servingWeight: Weight?,
        packageWeight: Weight?,
    ): FoodSnapshotQuantity =
        when (current) {
            is FoodSnapshotQuantity.Serving ->
                if (servingWeight != null)
                    current.copy(servingWeight = servingWeight, packageWeight = packageWeight)
                else
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = current.absoluteWeight,
                        servingWeight = servingWeight,
                        packageWeight = packageWeight,
                    )

            is FoodSnapshotQuantity.Package ->
                if (packageWeight != null)
                    current.copy(packageWeight = packageWeight, servingWeight = servingWeight)
                else
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = current.absoluteWeight,
                        servingWeight = servingWeight,
                        packageWeight = packageWeight,
                    )

            is FoodSnapshotQuantity.Weight ->
                current.copy(
                    absoluteWeight = current.absoluteWeight,
                    servingWeight = servingWeight,
                    packageWeight = packageWeight,
                )
        }
}
