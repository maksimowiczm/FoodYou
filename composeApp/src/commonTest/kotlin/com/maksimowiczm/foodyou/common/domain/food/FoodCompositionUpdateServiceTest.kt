package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.grams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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

        val componentToUpdate =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = FoodName(fallback = "Old Name"),
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = FoodName(fallback = "Other"),
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
            )

        val subComposition = FoodComposition(listOf(componentToUpdate, otherComponent))

        val compositeComponent =
            FoodCompositionComponent.Composite(
                identity = compositeId,
                name = FoodName(fallback = "Composite"),
                quantity = FoodComponentComponentQuantity.Weight(200.grams),
                composition = subComposition,
            )

        val mainComposition = FoodComposition(listOf(compositeComponent))

        val updatedComposition =
            FoodCompositionUpdateService.update(
                composition = mainComposition,
                identity = targetId,
                name = newName,
                nutritionFacts = newNutrition,
                servingWeight = null,
                packageWeight = null,
            )

        val updatedComposite =
            updatedComposition.components.first() as FoodCompositionComponent.Composite
        val updatedSubSimple =
            updatedComposite.composition.components.first() as FoodCompositionComponent.Simple

        assertEquals(newName, updatedSubSimple.name)
        assertEquals(newNutrition, updatedSubSimple.nutritionFacts)
        // Verify other component remained unchanged
        val unchangedSubSimple =
            updatedComposite.composition.components[1] as FoodCompositionComponent.Simple
        assertEquals(otherId, unchangedSubSimple.identity)
        assertEquals("Other", unchangedSubSimple.name.fallback)
    }

    @Test
    fun remove_should_remove_component_by_identity() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val otherId = FoodCompositionComponentIdentity.OpenFoodFacts("789")

        val componentToRemove =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
            )

        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = otherId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
            )

        val composition = FoodComposition(listOf(componentToRemove, otherComponent))

        val updated = FoodCompositionUpdateService.remove(composition, targetId)

        assertEquals(1, updated?.components?.size)
        assertEquals(otherId, updated?.components?.first()?.identity)
    }

    @Test
    fun remove_should_return_null_if_last_component_is_removed() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
            )
        val composition = FoodComposition(listOf(component))

        val updated = FoodCompositionUpdateService.remove(composition, targetId)

        assertNull(updated)
    }

    @Test
    fun remove_should_remove_recursively() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val compositeId = FoodCompositionComponentIdentity.Recipe(Uuid.random())

        val subComponent =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
            )
        val subComposition = FoodComposition(listOf(subComponent))

        val composite =
            FoodCompositionComponent.Composite(
                identity = compositeId,
                name = dummyName,
                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                composition = subComposition,
            )

        val mainComposition = FoodComposition(listOf(composite))

        // Removing the only component in sub-composition should make the sub-composition null,
        // which should make the composite component null, which should make the main composition
        // null.
        val updated = FoodCompositionUpdateService.remove(mainComposition, targetId)

        assertNull(updated)
    }

    @Test
    fun update_should_update_serving_weight_if_provided() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Serving(quantity = 2.0, servingWeight = 30.grams),
            )
        val composition = FoodComposition(listOf(component))

        val newServingWeight = 40.grams
        val updated =
            FoodCompositionUpdateService.update(
                composition = composition,
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                servingWeight = newServingWeight,
                packageWeight = null,
            )

        val updatedComponent = updated.components.first() as FoodCompositionComponent.Simple
        val updatedQuantity = updatedComponent.quantity as FoodComponentComponentQuantity.Serving
        assertEquals(newServingWeight, updatedQuantity.servingWeight)
        assertEquals(80.grams, updatedComponent.quantity.absoluteWeight)
    }

    @Test
    fun update_should_update_package_weight_if_provided() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Package(
                        quantity = 0.5,
                        packageWeight = 500.grams,
                    ),
            )
        val composition = FoodComposition(listOf(component))

        val newPackageWeight = 600.grams
        val updated =
            FoodCompositionUpdateService.update(
                composition = composition,
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                servingWeight = null,
                packageWeight = newPackageWeight,
            )

        val updatedComponent = updated.components.first() as FoodCompositionComponent.Simple
        val updatedQuantity = updatedComponent.quantity as FoodComponentComponentQuantity.Package
        assertEquals(newPackageWeight, updatedQuantity.packageWeight)
        assertEquals(300.grams, updatedComponent.quantity.absoluteWeight)
    }

    @Test
    fun update_should_update_composite_serving_weight_if_provided() {
        val targetId = FoodCompositionComponentIdentity.Recipe(Uuid.random())
        val subComposition =
            FoodComposition(
                listOf(
                    FoodCompositionComponent.Simple(
                        identity = FoodCompositionComponentIdentity.OpenFoodFacts("1"),
                        name = dummyName,
                        nutritionFacts = NutritionFacts(),
                        quantity = FoodComponentComponentQuantity.Weight(100.grams),
                    )
                )
            )
        val component =
            FoodCompositionComponent.Composite(
                identity = targetId,
                name = dummyName,
                quantity =
                    FoodComponentComponentQuantity.Serving(
                        quantity = 1.0,
                        servingWeight = 100.grams,
                    ),
                composition = subComposition,
            )
        val composition = FoodComposition(listOf(component))

        val newServingWeight = 50.grams
        val updated =
            FoodCompositionUpdateService.update(
                composition = composition,
                identity = targetId,
                name = dummyName,
                newComposition = subComposition,
                servingWeight = newServingWeight,
                packageWeight = null,
            )

        val updatedComponent = updated.components.first() as FoodCompositionComponent.Composite
        val updatedQuantity = updatedComponent.quantity as FoodComponentComponentQuantity.Serving
        assertEquals(newServingWeight, updatedQuantity.servingWeight)
        assertEquals(50.grams, updatedComponent.quantity.absoluteWeight)
    }

    @Test
    fun update_should_convert_serving_to_weight_if_serving_weight_becomes_null() {
        val targetId = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val component =
            FoodCompositionComponent.Simple(
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Serving(quantity = 2.0, servingWeight = 30.grams),
            )
        val composition = FoodComposition(listOf(component))

        val updated =
            FoodCompositionUpdateService.update(
                composition = composition,
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                servingWeight = null,
                packageWeight = null,
            )

        val updatedComponent = updated.components.first() as FoodCompositionComponent.Simple
        // Should be converted to Weight quantity
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
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Package(
                        quantity = 0.5,
                        packageWeight = 500.grams,
                    ),
            )
        val composition = FoodComposition(listOf(component))

        val updated =
            FoodCompositionUpdateService.update(
                composition = composition,
                identity = targetId,
                name = dummyName,
                nutritionFacts = NutritionFacts(),
                servingWeight = null,
                packageWeight = null,
            )

        val updatedComponent = updated.components.first() as FoodCompositionComponent.Simple
        // Should be converted to Weight quantity
        val updatedQuantity = updatedComponent.quantity as FoodComponentComponentQuantity.Weight
        assertEquals(250.grams, updatedQuantity.weight)
    }
}
