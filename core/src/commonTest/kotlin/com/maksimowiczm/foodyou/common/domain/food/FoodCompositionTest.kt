package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.grams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class FoodCompositionTest {

    private val dummyIdentity = FoodCompositionComponentIdentity.OpenFoodFacts("123")
    private val dummyName = FoodName(fallback = "Test Food")

    @Test
    fun totalWeight_should_be_sum_of_all_components_weights() {
        val component1 =
            FoodCompositionComponent.Simple(
                identity = dummyIdentity,
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
        val component2 =
            FoodCompositionComponent.Simple(
                identity = dummyIdentity,
                name = dummyName,
                image = null,
                nutritionFacts = NutritionFacts(),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 250.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val composition = listOf(component1, component2)

        assertEquals(350.grams, composition.totalWeight)
    }

    @Test
    fun nutritionFacts_should_be_normalized_to_100g() {
        // Component A: 50g total, 10g protein per 100g -> 5g protein absolute
        val componentA =
            FoodCompositionComponent.Simple(
                identity = dummyIdentity,
                name = FoodName(fallback = "A"),
                image = null,
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 50.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        // Component B: 150g total, 20g protein per 100g -> 30g protein absolute
        val componentB =
            FoodCompositionComponent.Simple(
                identity = dummyIdentity,
                name = FoodName(fallback = "B"),
                image = null,
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(20.grams)),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 150.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val composition = listOf(componentA, componentB)

        // Total weight = 200g
        // Total protein = 35g
        // Normalized protein per 100g = 35 / 200 * 100 = 17.5g
        assertEquals(200.grams, composition.totalWeight)
        assertEquals(NutrientValue.Complete(17.5.grams), composition.nutritionFacts.proteins)
    }

    @Test
    fun composite_components_should_calculate_nutrition_correctly() {
        // Sub-composition: 100g of something with 10g protein
        val subComponent =
            FoodCompositionComponent.Simple(
                identity = dummyIdentity,
                name = FoodName(fallback = "Sub"),
                image = null,
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 100.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        val subComponents = listOf(subComponent)

        // Main composition: 50g of sub-composition + 50g of another component (20g protein/100g)
        val compositeComponent =
            FoodCompositionComponent.Composite(
                identity = FoodCompositionComponentIdentity.Recipe(Uuid.random()),
                name = FoodName(fallback = "Composite"),
                image = null,
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 50.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
                components = subComponents,
            )
        val otherComponent =
            FoodCompositionComponent.Simple(
                identity = dummyIdentity,
                name = FoodName(fallback = "Other"),
                image = null,
                nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(20.grams)),
                quantity =
                    FoodComponentComponentQuantity.Weight(
                        absoluteWeight = 50.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )

        val mainComposition = listOf(compositeComponent, otherComponent)

        // Sub-composition normalized nutrition: 10g protein / 100g
        // Composite component (50g): 5g protein
        // Other component (50g): 10g protein
        // Total weight: 100g
        // Total protein: 15g
        // Normalized protein: 15g / 100g * 100 = 15g
        assertEquals(100.grams, mainComposition.totalWeight)
        assertEquals(NutrientValue.Complete(15.grams), mainComposition.nutritionFacts.proteins)
    }

    @Test
    fun allComponentIdentities_should_return_all_identities_recursively() {
        val id1 = FoodCompositionComponentIdentity.UserProduct(Uuid.random())
        val id2 = FoodCompositionComponentIdentity.OpenFoodFacts("456")
        val id3 = FoodCompositionComponentIdentity.Recipe(Uuid.random())
        val id4 = FoodCompositionComponentIdentity.FoodDataCentral(789)

        val subComponents =
            listOf(
                FoodCompositionComponent.Simple(
                    identity = id1,
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
            )

        val composite =
            FoodCompositionComponent.Composite(
                identity = id3,
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

        val simple2 =
            FoodCompositionComponent.Simple(
                identity = id2,
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

        val simple4 =
            FoodCompositionComponent.Simple(
                identity = id4,
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

        val composition = listOf(composite, simple2, simple4)

        val expected = setOf(id1, id2, id3, id4)
        assertEquals(expected, composition.allComponentIdentities)
    }
}
