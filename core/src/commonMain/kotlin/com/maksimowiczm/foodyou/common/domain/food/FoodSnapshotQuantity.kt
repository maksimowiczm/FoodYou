package com.maksimowiczm.foodyou.common.domain.food

import kotlinx.serialization.Serializable

/** Represents the quantity of a food component, expressed in one of several measurement forms. */
@Serializable
sealed interface FoodSnapshotQuantity {
    val servingQuantity: AbsoluteQuantity?
    val packageQuantity: AbsoluteQuantity?
    val absoluteQuantity: AbsoluteQuantity

    /** A quantity expressed as a direct weight measurement. */
    @Serializable
    data class Weight(
        val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight,
        override val servingQuantity: AbsoluteQuantity?,
        override val packageQuantity: AbsoluteQuantity?,
    ) : FoodSnapshotQuantity {
        override val absoluteQuantity: AbsoluteQuantity = AbsoluteQuantity.Weight(absoluteWeight)
    }

    /** A quantity expressed as a direct volume measurement. */
    @Serializable
    data class Volume(
        val absoluteVolume: com.maksimowiczm.foodyou.common.domain.Volume,
        override val servingQuantity: AbsoluteQuantity?,
        override val packageQuantity: AbsoluteQuantity?,
    ) : FoodSnapshotQuantity {
        override val absoluteQuantity: AbsoluteQuantity = AbsoluteQuantity.Volume(absoluteVolume)
    }

    /** A quantity expressed as a number of packages. */
    @Serializable
    data class Package(
        val packages: Double,
        override val servingQuantity: AbsoluteQuantity?,
        override val packageQuantity: AbsoluteQuantity,
    ) : FoodSnapshotQuantity {
        override val absoluteQuantity: AbsoluteQuantity = packageQuantity * packages
    }

    /** A quantity expressed as a number of servings. */
    @Serializable
    data class Serving(
        val servings: Double,
        override val servingQuantity: AbsoluteQuantity,
        override val packageQuantity: AbsoluteQuantity?,
    ) : FoodSnapshotQuantity {
        override val absoluteQuantity: AbsoluteQuantity = servingQuantity * servings
    }
}

fun FoodSnapshotQuantity.toQuantity(): Quantity =
    when (this) {
        is FoodSnapshotQuantity.Package -> toQuantity()
        is FoodSnapshotQuantity.Serving -> toQuantity()
        is FoodSnapshotQuantity.Weight -> toQuantity()
        is FoodSnapshotQuantity.Volume -> toQuantity()
    }

fun FoodSnapshotQuantity.Weight.toQuantity() = absoluteWeight.toAbsoluteQuantity()

fun FoodSnapshotQuantity.Volume.toQuantity() = absoluteVolume.toAbsoluteQuantity()

fun FoodSnapshotQuantity.Package.toQuantity() = PackageQuantity(packages)

fun FoodSnapshotQuantity.Serving.toQuantity() = ServingQuantity(servings)
