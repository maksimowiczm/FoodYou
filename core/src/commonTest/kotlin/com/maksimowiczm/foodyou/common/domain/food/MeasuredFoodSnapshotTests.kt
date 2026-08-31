package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.grams
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class MeasuredFoodSnapshotTests {

    private val dummyIdentity = FoodSnapshotId.OpenFoodFacts("123")
    private val dummyName = FoodName(fallback = "Test Food")

    @Test
    fun totalWeight_should_be_sum_of_all_components_weights() {
        val component1 =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = dummyIdentity,
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
        val component2 =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = dummyIdentity,
                        name = dummyName,
                        brand = null,
                        image = null,
                        nutritionFacts = NutritionFacts(),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
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
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = dummyIdentity,
                        name = FoodName(fallback = "A"),
                        brand = null,
                        image = null,
                        nutritionFacts =
                            NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 50.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        // Component B: 150g total, 20g protein per 100g -> 30g protein absolute
        val componentB =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = dummyIdentity,
                        name = FoodName(fallback = "B"),
                        brand = null,
                        image = null,
                        nutritionFacts =
                            NutritionFacts(proteins = NutrientValue.Complete(20.grams)),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
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
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = dummyIdentity,
                        name = FoodName(fallback = "Sub"),
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
        val subComponents = listOf(subComponent)

        // Main composition: 50g of sub-composition + 50g of another component (20g protein/100g)
        val compositeComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    CompositeFoodSnapshot(
                        id = FoodSnapshotId.UserRecipe(Uuid.random()),
                        name = FoodName(fallback = "Composite"),
                        brand = null,
                        image = null,
                        components = subComponents,
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 50.grams,
                        servingWeight = null,
                        packageWeight = null,
                    ),
            )
        val otherComponent =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = dummyIdentity,
                        name = FoodName(fallback = "Other"),
                        brand = null,
                        image = null,
                        nutritionFacts =
                            NutritionFacts(proteins = NutrientValue.Complete(20.grams)),
                    ),
                quantity =
                    FoodSnapshotQuantity.Weight(
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
        val id1 = FoodSnapshotId.UserProduct(Uuid.random())
        val id2 = FoodSnapshotId.OpenFoodFacts("456")
        val id3 = FoodSnapshotId.UserRecipe(Uuid.random())
        val id4 = FoodSnapshotId.FoodDataCentral(789)

        val subComponents =
            listOf(
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = id1,
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
            )

        val composite =
            MeasuredFoodSnapshot(
                snapshot =
                    CompositeFoodSnapshot(
                        id = id3,
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

        val simple2 =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = id2,
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

        val simple4 =
            MeasuredFoodSnapshot(
                snapshot =
                    LeafFoodSnapshot(
                        id = id4,
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

        val composition = listOf(composite, simple2, simple4)

        val expected = setOf(id1, id2, id3, id4)
        assertEquals(expected, composition.allComponentIdentities)
    }
}
