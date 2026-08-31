package com.maksimowiczm.foodyou.common.domain.food

import kotlinx.serialization.Serializable

/**
 * Represents the quantity of a food component, expressed in one of several measurement forms.
 *
 * @property servingWeight Optional weight of a single serving of this component
 * @property packageWeight Optional weight of a single package of this component
 * @property absoluteWeight The total weight of this component, including serving and package
 *   weights if applicable
 */
@Serializable
sealed interface FoodSnapshotQuantity {
    val servingWeight: com.maksimowiczm.foodyou.common.domain.Weight?
    val packageWeight: com.maksimowiczm.foodyou.common.domain.Weight?
    val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight

    /**
     * A quantity expressed as a direct weight measurement.
     *
     * @property absoluteWeight The explicit weight of the component.
     */
    @Serializable
    data class Weight(
        override val servingWeight: com.maksimowiczm.foodyou.common.domain.Weight?,
        override val packageWeight: com.maksimowiczm.foodyou.common.domain.Weight?,
        override val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight,
    ) : FoodSnapshotQuantity

    /**
     * A quantity expressed as a number of packages.
     *
     * @property packages The number of packages.
     */
    @Serializable
    data class Package(
        val packages: Double,
        override val servingWeight: com.maksimowiczm.foodyou.common.domain.Weight?,
        override val packageWeight: com.maksimowiczm.foodyou.common.domain.Weight,
    ) : FoodSnapshotQuantity {
        override val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight =
            packageWeight * packages
    }

    /**
     * A quantity expressed as a number of servings.
     *
     * @property servings The number of servings.
     */
    @Serializable
    data class Serving(
        val servings: Double,
        override val servingWeight: com.maksimowiczm.foodyou.common.domain.Weight,
        override val packageWeight: com.maksimowiczm.foodyou.common.domain.Weight?,
    ) : FoodSnapshotQuantity {
        override val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight =
            servingWeight * servings
    }
}

fun FoodSnapshotQuantity.toQuantity(): Quantity =
    when (this) {
        is FoodSnapshotQuantity.Package -> toQuantity()
        is FoodSnapshotQuantity.Serving -> toQuantity()
        is FoodSnapshotQuantity.Weight -> toQuantity()
    }

fun FoodSnapshotQuantity.Weight.toQuantity() = absoluteWeight.toAbsoluteQuantity()

fun FoodSnapshotQuantity.Package.toQuantity() = PackageQuantity(packages)

fun FoodSnapshotQuantity.Serving.toQuantity() = ServingQuantity(servings)
