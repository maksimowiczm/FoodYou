package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.grams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class FoodCompositionUpdateServiceTest {

    private val dummyName = FoodName(fallback = "Test Food")

    @Test
    fun update_should_recursively_update_components_matching_identity() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val otherId = FoodCompositionComponentIdentity.OpenFoodFacts("789")
        val compositeId = FoodCompositionComponentIdentity.Recipe(Uuid.random())

        val newName = FoodName(fallback = "Updated Name")
        val newNutrition = NutritionFacts(proteins = NutrientValue.Complete(50.grams))
        val newImage = BlobDigest("new-image")

        val componentToUpdate =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = FoodName(fallback = "Old Name"),
                image = null,
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = FoodName(fallback = "Other"),
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val subComponents = listOf(componentToUpdate, otherComponent)

        val compositeComponent =
            FoodCompositionComponent.Composite(
                identity = compositeId,
                name = FoodName(fallback = "Composite"),
                image = null,
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 200.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
                components = subComponents,
            )

        val mainComponents = listOf(compositeComponent)

        val updatedComponents =
            FoodCompositionUpdateService.update(
                components = mainComponents,
                identity = targetId,
                name = newName,
                nutritionFacts = newNutrition,
                quantity = { it },
                image = FoodCompositionComponentImage.Blob(newImage),
            )

        val updatedComposite = updatedComponents.first() as FoodCompositionComponent.Composite
        val updatedSubSimple =
            updatedComposite.components.first() as FoodCompositionComponent.Simple

        assertEquals(newName, updatedSubSimple.name)
        assertEquals(newNutrition, updatedSubSimple.nutritionFacts)
        assertEquals(FoodCompositionComponentImage.Blob(newImage), updatedSubSimple.image)
        // Verify other component remained unchanged
        val unchangedSubSimple = updatedComposite.components[1] as FoodCompositionComponent.Simple
        assertEquals(otherId, unchangedSubSimple.identity)
        assertEquals("Other", unchangedSubSimple.name.fallback)
        assertEquals(null, unchangedSubSimple.image)
    }

    @Test
    fun remove_should_remove_component_by_identity() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val otherId = FoodCompositionComponentIdentity.OpenFoodFacts("789")

        val componentToRemove =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val components = listOf(componentToRemove, otherComponent)

        val updated = FoodCompositionUpdateService.remove(components, targetId)

        assertEquals(1, updated.size)
        assertEquals(otherId, updated.first().identity)
    }

    @Test
    fun remove_should_return_empty_if_last_component_is_removed() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        val components = listOf(component)

        val updated = FoodCompositionUpdateService.remove(components, targetId)

        assertTrue(updated.isEmpty())
    }

    @Test
    fun remove_should_remove_recursively() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val compositeId = FoodCompositionComponentIdentity.Recipe(Uuid.random())

        val subComponent =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        val subComponents = listOf(subComponent)

        val composite =
            FoodCompositionComponent.Composite(
                identity = compositeId,
                name = dummyName,
                image = null,
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
                components = subComponents,
            )

        val mainComponents = listOf(composite)

        val updated = FoodCompositionUpdateService.remove(mainComponents, targetId)

        assertEquals(1, updated.size)
        val updatedComposite = updated.first()

        assertIs<FoodCompositionComponent.Composite>(updatedComposite)
        assertEquals(compositeId, updatedComposite.identity)
        assertTrue(updatedComposite.components.isEmpty())
    }

    @Test
    fun unlink_should_replace_component_with_anonymous_counterpart() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val otherId = FoodCompositionComponentIdentity.OpenFoodFacts("789")

        val componentToUnlink =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = FoodName(fallback = "Unlinked"),
                image = null,
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val components = listOf(componentToUnlink, otherComponent)

        val updated = FoodCompositionUpdateService.unlink(components, targetId)

        assertEquals(2, updated.size)
        val unlinked = updated[0]
        val unchanged = updated[1]

        assertEquals("Unlinked", unlinked.name.fallback)
        val identity = unlinked.identity
        assertEquals(true, identity is FoodCompositionComponentIdentity.Anonymous)
        assertEquals(otherId, unchanged.identity)
    }
}
