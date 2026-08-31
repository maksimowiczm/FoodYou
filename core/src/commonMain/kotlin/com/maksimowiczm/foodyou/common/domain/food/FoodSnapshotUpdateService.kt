package com.maksimowiczm.foodyou.common.domain.food

/**
 * Domain service for updating and transforming food composition structures.
 *
 * This service handles the recursive traversal and transformation of the component tree, keeping
 * the domain entities ([MeasuredFoodSnapshot]) focused on representing the data.
 */
object FoodSnapshotUpdateService {

    /**
     * Updates all components identified by [id] with the [transform]. The update is propagated
     * through the entire tree.
     */
    fun update(
        components: List<MeasuredFoodSnapshot>,
        id: FoodSnapshotId,
        transform: (MeasuredFoodSnapshot) -> MeasuredFoodSnapshot,
    ): List<MeasuredFoodSnapshot> =
        components.transform(id) { component ->
            transform(component)
        }

    /**
     * Removes all components identified by [id] from the tree. If a composite component becomes
     * empty after removal, it is also removed.
     */
    fun remove(
        components: List<MeasuredFoodSnapshot>,
        id: FoodSnapshotId,
    ): List<MeasuredFoodSnapshot> = components.transform(id) { null }

    /** Replaces all components identified by [id] with their anonymous counterparts. */
    fun unlink(
        components: List<MeasuredFoodSnapshot>,
        id: FoodSnapshotId,
    ): List<MeasuredFoodSnapshot> = components.transform(id) { it.anonymize() }

    private fun List<MeasuredFoodSnapshot>.transform(
        targetId: FoodSnapshotId,
        transformer: (MeasuredFoodSnapshot) -> MeasuredFoodSnapshot?,
    ): List<MeasuredFoodSnapshot> {
        val updated = mapNotNull { it.transform(targetId, transformer) }
        return if (updated == this) this else updated
    }

    private fun MeasuredFoodSnapshot.transform(
        targetId: FoodSnapshotId,
        transformer: (MeasuredFoodSnapshot) -> MeasuredFoodSnapshot?,
    ): MeasuredFoodSnapshot? {
        if (identity == targetId) return transformer(this)

        val snapshot = snapshot
        if (snapshot !is CompositeFoodSnapshot) return this

        val updatedInner = snapshot.components.transform(targetId, transformer)
        if (updatedInner === snapshot.components) return this
        if (updatedInner.isEmpty()) return null

        return copy(snapshot = snapshot.copy(components = updatedInner))
    }
}
