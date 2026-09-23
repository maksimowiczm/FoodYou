package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId

/** Repository for tracking which identified components are used in food diary entries. */
interface FoodDiaryCompositionRepository {
    /**
     * Finds all food diary entries that contain the given snapshot id.
     *
     * @param id The id of the product or recipe.
     * @return A list of identities for food diary entries that use the specified component.
     */
    suspend fun findEntriesUsing(id: FoodSnapshotId.Tracked): List<FoodDiaryEntryId>

    /**
     * Saves the association between a food diary entry and its component identities.
     *
     * @param id The id of the food diary entry.
     * @param trackedIds The set of all component identities used in the entry.
     */
    suspend fun saveReferences(
        id: FoodDiaryEntryId,
        trackedIds: Set<FoodSnapshotId.Tracked>,
    )

    /**
     * Removes all recorded component associations for a specific food diary entry.
     *
     * @param id The id of the food diary entry to clear references for.
     */
    suspend fun removeReferences(id: FoodDiaryEntryId)
}
