package com.maksimowiczm.foodyou.common.domain.food

/** Domain service for managing and transforming [FoodSnapshotQuantity] instances. */
object FoodSnapshotQuantityUpdateService {

    /**
     * Maps a general [Quantity] to a component-specific [FoodSnapshotQuantity], incorporating the
     * provided serving and package quantity metadata.
     */
    fun map(
        quantity: Quantity,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
    ): FoodSnapshotQuantity =
        when (quantity) {
            is AbsoluteQuantity.Weight ->
                FoodSnapshotQuantity.Weight(
                    absoluteWeight = quantity.weight,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                )

            is AbsoluteQuantity.Volume ->
                FoodSnapshotQuantity.Volume(
                    absoluteVolume = quantity.volume,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                )

            is PackageQuantity -> {
                requireNotNull(packageQuantity) { "Package quantity requires package quantity" }
                FoodSnapshotQuantity.Package(
                    packages = quantity.packages,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                )
            }

            is ServingQuantity -> {
                requireNotNull(servingQuantity) { "Serving quantity requires serving quantity" }
                FoodSnapshotQuantity.Serving(
                    servings = quantity.servings,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                )
            }
        }

    /**
     * Updates an existing [FoodSnapshotQuantity] with new serving and package quantities.
     *
     * If a quantity variant (e.g., [FoodSnapshotQuantity.Serving]) requires a quantity that becomes
     * `null`, it is converted to a direct [FoodSnapshotQuantity.Weight] or
     * [FoodSnapshotQuantity.Volume] using the [FoodSnapshotQuantity.absoluteQuantity].
     */
    fun update(
        current: FoodSnapshotQuantity,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
    ): FoodSnapshotQuantity =
        when (current) {
            is FoodSnapshotQuantity.Serving ->
                if (servingQuantity != null)
                    current.copy(
                        servingQuantity = servingQuantity,
                        packageQuantity = packageQuantity,
                    )
                else
                    when (val abs = current.absoluteQuantity) {
                        is AbsoluteQuantity.Weight ->
                            FoodSnapshotQuantity.Weight(
                                absoluteWeight = abs.weight,
                                servingQuantity = servingQuantity,
                                packageQuantity = packageQuantity,
                            )
                        is AbsoluteQuantity.Volume ->
                            FoodSnapshotQuantity.Volume(
                                absoluteVolume = abs.volume,
                                servingQuantity = servingQuantity,
                                packageQuantity = packageQuantity,
                            )
                    }

            is FoodSnapshotQuantity.Package ->
                if (packageQuantity != null)
                    current.copy(
                        packageQuantity = packageQuantity,
                        servingQuantity = servingQuantity,
                    )
                else
                    when (val abs = current.absoluteQuantity) {
                        is AbsoluteQuantity.Weight ->
                            FoodSnapshotQuantity.Weight(
                                absoluteWeight = abs.weight,
                                servingQuantity = servingQuantity,
                                packageQuantity = packageQuantity,
                            )
                        is AbsoluteQuantity.Volume ->
                            FoodSnapshotQuantity.Volume(
                                absoluteVolume = abs.volume,
                                servingQuantity = servingQuantity,
                                packageQuantity = packageQuantity,
                            )
                    }

            is FoodSnapshotQuantity.Weight ->
                current.copy(
                    absoluteWeight = current.absoluteWeight,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                )

            is FoodSnapshotQuantity.Volume ->
                current.copy(
                    absoluteVolume = current.absoluteVolume,
                    servingQuantity = servingQuantity,
                    packageQuantity = packageQuantity,
                )
        }
}
