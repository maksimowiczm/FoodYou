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
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = FoodName(fallback = "Other"),
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
            )

        val subComponents = listOf(componentToUpdate, otherComponent)

        val compositeComponent =
            FoodCompositionComponent.Composite(
                identity = compositeId,
                name = FoodName(fallback = "Composite"),
                image = null,
                quantity = FoodComponentComponentQuantity.Weight(200.grams),
                components = subComponents,
                servingWeight = null,
                packageWeight = null,
            )

        val mainComponents = listOf(compositeComponent)

        val updatedComponents =
            FoodCompositionUpdateService.update(
                components = mainComponents,
                identity = targetId,
                name = newName,
                image = FoodCompositionComponentImage.Blob(newImage),
                nutritionFacts = newNutrition,
                servingWeight = null,
                packageWeight = null,
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
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
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
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
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
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
            )
        val subComponents = listOf(subComponent)

        val composite =
            FoodCompositionComponent.Composite(
                identity = compositeId,
                name = dummyName,
                image = null,
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                components = subComponents,
                servingWeight = null,
                packageWeight = null,
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
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                servingWeight = null,
                packageWeight = null,
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

    @Test
    fun update_should_update_serving_weight_if_provided() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Serving(servings = 2.0),
                servingWeight = 30.grams,
                packageWeight = null,
            )
        val components = listOf(component)

        val newServingWeight = 40.grams
        val updated =
            FoodCompositionUpdateService.update(
                components = components,
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                servingWeight = newServingWeight,
                packageWeight = null,
            )

        val updatedComponent = updated.first() as FoodCompositionComponent.Simple
        assertEquals(newServingWeight, updatedComponent.servingWeight)
        assertEquals(80.grams, updatedComponent.absoluteWeight)
    }

    @Test
    fun update_should_update_package_weight_if_provided() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Package(packages = 0.5),
                servingWeight = null,
                packageWeight = 500.grams,
            )
        val components = listOf(component)

        val newPackageWeight = 600.grams
        val updated =
            FoodCompositionUpdateService.update(
                components = components,
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                servingWeight = null,
                packageWeight = newPackageWeight,
            )

        val updatedComponent = updated.first() as FoodCompositionComponent.Simple
        assertEquals(newPackageWeight, updatedComponent.packageWeight)
        assertEquals(300.grams, updatedComponent.absoluteWeight)
    }

    @Test
    fun update_should_update_composite_serving_weight_if_provided() {
        val targetId = FoodCompositionComponentIdentity.Recipe(Uuid.random())
        val subComponents =
            listOf(
                FoodCompositionComponent.Simple(
                    identity = FoodCompositionComponentIdentity.OpenFoodFacts("1"),
                    name = dummyName,
                    image = null,
                    nutritionFacts = NutritionFacts(),
                    quantity = FoodComponentComponentQuantity.Weight(100.grams),
                    servingWeight = null,
                    packageWeight = null,
                )
            )
        val component =
            FoodCompositionComponent.Composite(
                identity = targetId,
                name = dummyName,
                image = null,
                quantity = FoodComponentComponentQuantity.Serving(servings = 1.0),
                servingWeight = 100.grams,
                components = subComponents,
                packageWeight = null,
            )
        val components = listOf(component)

        val newServingWeight = 50.grams
        val updated =
            FoodCompositionUpdateService.update(
                components = components,
                identity = targetId,
                name = dummyName,
                image = null,
                newComponents = subComponents,
                servingWeight = newServingWeight,
                packageWeight = null,
            )

        val updatedComponent = updated.first() as FoodCompositionComponent.Composite
        assertEquals(newServingWeight, updatedComponent.servingWeight)
        assertEquals(50.grams, updatedComponent.absoluteWeight)
    }

    @Test
    fun update_should_convert_serving_to_weight_if_serving_weight_becomes_null() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Serving(servings = 2.0),
                servingWeight = 30.grams,
                packageWeight = null,
            )
        val components = listOf(component)

        val updated =
            FoodCompositionUpdateService.update(
                components = components,
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                servingWeight = null,
                packageWeight = null,
            )

        val updatedComponent = updated.first() as FoodCompositionComponent.Simple
        // Should be converted to Weight servings
        val updatedQuantity = updatedComponent.quantity as FoodComponentComponentQuantity.Weight
        assertEquals(60.grams, updatedQuantity.weight)
    }

    @Test
    fun update_should_convert_package_to_weight_if_package_weight_becomes_null() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Package(packages = 0.5),
                servingWeight = null,
                packageWeight = 500.grams,
            )
        val components = listOf(component)

        val updated =
            FoodCompositionUpdateService.update(
                components = components,
                identity = targetId,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                servingWeight = null,
                packageWeight = null,
            )

        val updatedComponent = updated.first() as FoodCompositionComponent.Simple
        // Should be converted to Weight servings
        val updatedQuantity = updatedComponent.quantity as FoodComponentComponentQuantity.Weight
        assertEquals(250.grams, updatedQuantity.weight)
    }
}
