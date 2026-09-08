package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.grams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class FoodSnapshotUpdateServiceTest {

    private val dummyName = FoodName(fallback = "Test Food")

    @Test
    fun update_should_recursively_update_components_matching_id() {
        val targetId = FoodSnapshotId.UserProduct(Uuid.random())
        val otherId = FoodSnapshotId.OpenFoodFacts("789")
        val compositeId = FoodSnapshotId.UserRecipe(Uuid.random())

        val newSnapshot =
            LeafFoodSnapshot(
                id = targetId,
                name = FoodName(fallback = "Updated Name"),
                brand = "Brand",
                image = FoodSnapshotImage.Blob(BlobDigest("new-image")),
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(50.grams)),
            )

        val componentToUpdate =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = targetId,
                        name = FoodName(fallback = "Old Name"),
                        brand = null,
                        image = null,
                        nutritionFacts =
                            NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val otherComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = otherId,
                        name = FoodName(fallback = "Other"),
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val subComponents = listOf(componentToUpdate, otherComponent)

        val compositeComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    CompositeFoodSnapshot(
                        id = compositeId,
                        name = FoodName(fallback = "Composite"),
                        brand = null,
                        image = null,
                        components = subComponents,
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 200.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val mainComponents = listOf(compositeComponent)

        val updatedComponents =
            FoodSnapshotUpdateService.update(
                components = mainComponents,
                id = targetId,
                transform = { it.copy(snapshot = newSnapshot) },
            )

        val updatedComposite = updatedComponents.first()
        val compositeSnapshot = updatedComposite.snapshot as CompositeFoodSnapshot
        val updatedSubSimple = compositeSnapshot.components.first()
        val simpleSnapshot = updatedSubSimple.snapshot as LeafFoodSnapshot

        assertEquals(newSnapshot.name, updatedSubSimple.name)
        assertEquals(newSnapshot.nutritionFacts, updatedSubSimple.nutritionFacts)
        assertEquals(newSnapshot.image, updatedSubSimple.image)
        assertEquals(newSnapshot.brand, simpleSnapshot.brand)

        // Verify other component remained unchanged
        val unchangedSubSimple = compositeSnapshot.components[1]
        assertEquals(otherId, unchangedSubSimple.id)
        assertEquals("Other", unchangedSubSimple.name.fallback)
        assertEquals(null, unchangedSubSimple.image)
    }

    @Test
    fun remove_should_remove_component_by_id() {
        val targetId = FoodSnapshotId.UserProduct(Uuid.random())
        val otherId = FoodSnapshotId.OpenFoodFacts("789")

        val componentToRemove =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = targetId,
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val otherComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = otherId,
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val components = listOf(componentToRemove, otherComponent)

        val updated = FoodSnapshotUpdateService.remove(components, targetId)

        assertEquals(1, updated.size)
        assertEquals(otherId, updated.first().id)
    }

    @Test
    fun remove_should_return_empty_if_last_component_is_removed() {
        val targetId = FoodSnapshotId.UserProduct(Uuid.random())
        val component =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = targetId,
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        val components = listOf(component)

        val updated = FoodSnapshotUpdateService.remove(components, targetId)

        assertTrue(updated.isEmpty())
    }

    @Test
    fun remove_should_prune_empty_composites() {
        val targetId = FoodSnapshotId.UserProduct(Uuid.random())
        val compositeId = FoodSnapshotId.UserRecipe(Uuid.random())

        val subComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = targetId,
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        val subComponents = listOf(subComponent)

        val composite =
            MeasuredFoodSnapshot(
                snapshot =
                    CompositeFoodSnapshot(
                        id = compositeId,
                        name = dummyName,
                        brand = null,
                        image = null,
                        components = subComponents,
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val mainComponents = listOf(composite)

        val updated = FoodSnapshotUpdateService.remove(mainComponents, targetId)

        assertTrue(updated.isEmpty(), "Empty composite should have been pruned")
    }

    @Test
    fun remove_should_prune_nested_empty_composites_but_keep_non_empty_ones() {
        val targetId = FoodSnapshotId.UserProduct(Uuid.random())
        val composite1Id = FoodSnapshotId.UserRecipe(Uuid.random())
        val composite2Id = FoodSnapshotId.UserRecipe(Uuid.random())

        val subComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = targetId,
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val otherComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = FoodSnapshotId.OpenFoodFacts("other"),
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val composite1 =
            MeasuredFoodSnapshot(
                snapshot =
                    CompositeFoodSnapshot(
                        id = composite1Id,
                        name = FoodName(fallback = "Empty one"),
                        brand = null,
                        image = null,
                        components = listOf(subComponent),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val composite2 =
            MeasuredFoodSnapshot(
                snapshot =
                    CompositeFoodSnapshot(
                        id = composite2Id,
                        name = FoodName(fallback = "Non empty one"),
                        brand = null,
                        image = null,
                        components = listOf(subComponent, otherComponent),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 200.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val mainComponents = listOf(composite1, composite2)

        val updated = FoodSnapshotUpdateService.remove(mainComponents, targetId)

        assertEquals(1, updated.size)
        val updatedComposite2 = updated.first()
        assertEquals(composite2Id, updatedComposite2.id)
        val snapshot = updatedComposite2.snapshot as CompositeFoodSnapshot
        assertEquals(1, snapshot.components.size)
        assertEquals(
            "other",
            (snapshot.components.first().id as FoodSnapshotId.OpenFoodFacts).barcode,
        )
    }

    @Test
    fun unlink_should_replace_component_with_anonymous_counterpart() {
        val targetId = FoodSnapshotId.UserProduct(Uuid.random())
        val otherId = FoodSnapshotId.OpenFoodFacts("789")

        val componentToUnlink =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = targetId,
                        name = FoodName(fallback = "Unlinked"),
                        brand = null,
                        image = null,
                        nutritionFacts =
                            NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val otherComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = otherId,
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val components = listOf(componentToUnlink, otherComponent)

        val updated = FoodSnapshotUpdateService.unlink(components, targetId)

        assertEquals(2, updated.size)
        val unlinked = updated[0]
        val unchanged = updated[1]

        assertEquals("Unlinked", unlinked.name.fallback)
        val anonymousId = assertIs<FoodSnapshotId.Anonymous>(unlinked.id)
        assertEquals(targetId, anonymousId.trackedId)
        assertEquals(otherId, unchanged.id)
    }
}
