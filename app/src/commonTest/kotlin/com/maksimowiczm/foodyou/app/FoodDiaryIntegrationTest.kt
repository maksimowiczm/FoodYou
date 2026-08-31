package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity.Weight
import com.maksimowiczm.foodyou.common.domain.food.AnonymousFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.common.infrastructure.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.common.infrastructure.crypto.encryptString
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettings
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.koin.core.Koin
import org.koin.dsl.bind
import org.koin.dsl.module

class FoodDiaryIntegrationTest {
    private val profileId = ProfileId()
    private val mealId = MealIdentity()

    @Test
    fun updating_user_product_updates_diary_entries_using_it() = runTest {
        runKoin {
            // 1. Create a User Product
            val initialProductName =
                FoodName(english = "Initial Product", fallback = "Initial Product")
            val initialNutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
            val productId =
                userProductService.create(
                    name = initialProductName,
                    brand = "Brand",
                    barcode = null,
                    note = null,
                    imageBytes = null,
                    nutritionFacts = initialNutrition,
                    servingQuantity = null,
                    packageQuantity = null,
                    isLiquid = false,
                )

            // 2. Create a Diary Entry using this product
            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.id),
                            name = initialProductName,
                            brand = null,
                            image = null,
                            nutritionFacts = initialNutrition,
                        ),
                    quantity =
                        FoodSnapshotQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.id))
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(initialProductName, entry.composition.name)

            // 4. Update the User Product
            val updatedProductName =
                FoodName(english = "Updated Product", fallback = "Updated Product")
            val updatedNutrition = NutritionFacts(proteins = NutrientValue.Complete(20.grams))

            userProductService.edit(
                identity = productId,
                name = updatedProductName,
                brand = "Brand",
                barcode = null,
                note = null,
                imageBytes = null,
                nutritionFacts = updatedNutrition,
                servingQuantity = null,
                packageQuantity = null,
                isLiquid = false,
            )

            // 5. Verify the diary entry has been updated
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.composition.name == updatedProductName
                }

            val updatedComponent = updatedEntry.composition.snapshot as LeafFoodSnapshot
            assertEquals(updatedProductName, updatedComponent.name)
            assertEquals(updatedNutrition, updatedComponent.nutritionFacts)
        }
    }

    @Test
    fun updating_user_recipe_updates_diary_entries_using_it() = runTest {
        runKoin {
            // 1. Create a Recipe
            val initialRecipeName =
                FoodName(english = "Initial Recipe", fallback = "Initial Recipe")
            val recipeComponents =
                listOf(
                    MeasuredFoodSnapshot(
                        snapshot =
                            LeafFoodSnapshot(
                                id = FoodSnapshotId.OpenFoodFacts("1"),
                                name = FoodName(english = "Ingredient", fallback = "Ingredient"),
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
            val recipeId =
                userRecipeService.create(
                    name = initialRecipeName,
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components = recipeComponents,
                )

            // 2. Create a Diary Entry using this recipe
            val entryComposition =
                MeasuredFoodSnapshot(
                    snapshot =
                        CompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.id),
                            name = initialRecipeName,
                            brand = null,
                            image = null,
                            components = recipeComponents,
                        ),
                    quantity =
                        FoodSnapshotQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(
                    setOf(profileId),
                    entryComposition,
                    mealId,
                    Clock.System.now(),
                )

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.id))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 4. Update the Recipe
            val updatedRecipeName =
                FoodName(english = "Updated Recipe", fallback = "Updated Recipe")
            userRecipeService.edit(
                identity = recipeId,
                name = updatedRecipeName,
                note = "Updated",
                imageBytes = null,
                servings = 1.0,
                components = recipeComponents,
            )

            // 5. Verify the diary entry has been updated
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.composition.name == updatedRecipeName
                }

            val updatedComponent = updatedEntry.composition.snapshot as CompositeFoodSnapshot
            assertEquals(updatedRecipeName, updatedComponent.name)
        }
    }

    @Test
    fun updating_user_product_used_in_recipe_updates_diary_entry() = runTest {
        runKoin {
            // 1. Create a User Product
            val initialProductName = FoodName(english = "Product", fallback = "Product")
            val initialNutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
            val productId =
                userProductService.create(
                    name = initialProductName,
                    brand = "Brand",
                    barcode = null,
                    note = null,
                    imageBytes = null,
                    nutritionFacts = initialNutrition,
                    servingQuantity = null,
                    packageQuantity = null,
                    isLiquid = false,
                )

            // 2. Create a Recipe using this product
            val recipeComponents =
                listOf(
                    MeasuredFoodSnapshot(
                        snapshot =
                            LeafFoodSnapshot(
                                id = FoodSnapshotId.UserProduct(productId.id),
                                name = initialProductName,
                                brand = null,
                                image = null,
                                nutritionFacts = initialNutrition,
                            ),
                        quantity =
                            FoodSnapshotQuantity.Weight(
                                absoluteWeight = 100.grams,
                                servingWeight = null,
                                packageWeight = null,
                            ),
                    )
                )
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components = recipeComponents,
                )

            // 3. Create a Diary Entry using this recipe
            val entryComposition =
                MeasuredFoodSnapshot(
                    snapshot =
                        CompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.id),
                            name = FoodName(english = "Recipe", fallback = "Recipe"),
                            brand = null,
                            image = null,
                            components = recipeComponents,
                        ),
                    quantity =
                        FoodSnapshotQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(
                    setOf(profileId),
                    entryComposition,
                    mealId,
                    Clock.System.now(),
                )

            // 4. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.id))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 5. Update the User Product
            val updatedProductName =
                FoodName(english = "Updated Product", fallback = "Updated Product")
            userProductService.edit(
                identity = productId,
                name = updatedProductName,
                brand = "Brand",
                barcode = null,
                note = null,
                imageBytes = null,
                nutritionFacts = initialNutrition,
                servingQuantity = null,
                packageQuantity = null,
                isLiquid = false,
            )

            // 6. Verify the diary entry (and the nested recipe) has been updated
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first { entry ->
                    val composite = entry.composition.snapshot as CompositeFoodSnapshot
                    composite.components.first().name == updatedProductName
                }

            val composite = updatedEntry.composition.snapshot as CompositeFoodSnapshot
            val simple = composite.components.first().snapshot as LeafFoodSnapshot
            assertEquals(updatedProductName, simple.name)
        }
    }

    @Test
    fun deleting_user_product_removes_it_from_diary_entries() = runTest {
        runKoin {
            // 1. Create a User Product
            val productId =
                userProductService.create(
                    name = FoodName(english = "Product", fallback = "Product"),
                    brand = "Brand",
                    barcode = UserProductBarcode("123"),
                    note = null,
                    imageBytes = null,
                    servingQuantity = null,
                    packageQuantity = null,
                    isLiquid = false,
                    nutritionFacts = NutritionFacts(),
                )

            // 2. Create a Diary Entry using this product
            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.id),
                            name = FoodName(english = "To Delete", fallback = "To Delete"),
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
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.id))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 4. Delete the User Product
            userProductService.delete(productId, DeleteStrategy.Delete)

            // 5. Verify the diary entry has been deleted
            val deletedEntry = foodDiaryService.observe(entryId).first { it == null }
            assertEquals(null, deletedEntry)
        }
    }

    @Test
    fun unlinking_user_product_anonymizes_it_in_diary_entries() = runTest {
        runKoin {
            // 1. Create a User Product
            val productId =
                userProductService.create(
                    name = FoodName(english = "Product", fallback = "Product"),
                    brand = "Brand",
                    barcode = UserProductBarcode("123"),
                    note = null,
                    imageBytes = null,
                    servingQuantity = null,
                    packageQuantity = null,
                    isLiquid = false,
                    nutritionFacts = NutritionFacts(),
                )

            // 2. Create a Diary Entry using this product
            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.id),
                            name = FoodName(english = "To Unlink", fallback = "To Unlink"),
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
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.id))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 4. Unlink the User Product
            userProductService.delete(productId, DeleteStrategy.Unlink)

            // 5. Verify the diary entry has been anonymized
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.composition.identity is FoodSnapshotId.Anonymous
                }

            assertIs<FoodSnapshotId.Anonymous>(updatedEntry.composition.identity)
        }
    }

    @Test
    fun updating_user_product_serving_weight_updates_diary_entries_using_it_by_serving() = runTest {
        runKoin {
            // 1. Create a User Product with initial serving weight
            val productId =
                userProductService.create(
                    name = FoodName(english = "Product", fallback = "Product"),
                    brand = "Brand",
                    barcode = null,
                    note = null,
                    imageBytes = null,
                    nutritionFacts = NutritionFacts(),
                    servingQuantity = Weight(30.grams),
                    packageQuantity = null,
                    isLiquid = false,
                )

            // 2. Create a Diary Entry using 2 servings of this product
            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.id),
                            name = FoodName(english = "Product", fallback = "Product"),
                            brand = null,
                            image = null,
                            nutritionFacts = NutritionFacts(),
                        ),
                    quantity =
                        FoodSnapshotQuantity.Serving(
                            servings = 2.0,
                            servingWeight = 30.grams,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.id))
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(60.grams, entry.composition.quantity.absoluteWeight)

            // 4. Update product serving weight to 40g
            userProductService.edit(
                identity = productId,
                name = FoodName(english = "Product", fallback = "Product"),
                brand = "Brand",
                barcode = null,
                note = null,
                imageBytes = null,
                nutritionFacts = NutritionFacts(),
                servingQuantity = Weight(40.grams),
                packageQuantity = null,
                isLiquid = false,
            )

            // 5. Verify the diary entry has been updated and absolute weight is now 80g (2 * 40g)
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.composition.quantity.absoluteWeight == 80.grams
                }

            assertEquals(40.grams, updatedEntry.composition.quantity.servingWeight)
        }
    }

    @Test
    fun updating_open_food_facts_product_updates_diary_entries_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val barcode = "8000500179864"
                val identity = OpenFoodFactsProductIdentity(barcode)

                // 1. Create a Diary Entry using an OFF product
                val initialProductName = FoodName(english = "OFF Product", fallback = "OFF Product")
                val composition =
                    MeasuredFoodSnapshot(
                        snapshot =
                            LeafFoodSnapshot(
                                id = FoodSnapshotId.OpenFoodFacts(barcode),
                                name = initialProductName,
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
                val entryId =
                    foodDiaryService.create(
                        setOf(profileId),
                        composition,
                        mealId,
                        Clock.System.now(),
                    )

                // 2. Wait until entry is created and indexed
                waitForComposition(entryId, FoodSnapshotId.OpenFoodFacts(barcode))
                foodDiaryService.observe(entryId).filterNotNull().first()

                // 3. Trigger OFF refresh
                get<OpenFoodFactsService>().refresh(identity).expect("Refreshed OFF product")

                // 4. Wait for synchronizer to update the diary entry
                val updatedEntry =
                    foodDiaryService.observe(entryId).filterNotNull().first {
                        it.composition.name != initialProductName
                    }

                assertEquals(false, updatedEntry.composition.name == initialProductName)
            }
        }

    @Test
    fun updating_food_data_central_product_updates_diary_entries_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val fdcId = 2768188
                val identity = FoodDataCentralProductIdentity(fdcId)

                // 1. Create a Diary Entry using an FDC product
                val initialProductName = FoodName(english = "FDC Product", fallback = "FDC Product")
                val composition =
                    MeasuredFoodSnapshot(
                        snapshot =
                            LeafFoodSnapshot(
                                id = FoodSnapshotId.FoodDataCentral(fdcId),
                                name = initialProductName,
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
                val entryId =
                    foodDiaryService.create(
                        setOf(profileId),
                        composition,
                        mealId,
                        Clock.System.now(),
                    )

                // 2. Wait until entry is created and indexed
                waitForComposition(entryId, FoodSnapshotId.FoodDataCentral(fdcId))
                foodDiaryService.observe(entryId).filterNotNull().first()

                // 3. Trigger FDC refresh
                get<FoodDataCentralService>().refresh(identity).expect("Refreshed FDC product")

                // 4. Wait for synchronizer to update the diary entry
                val updatedEntry =
                    foodDiaryService.observe(entryId).filterNotNull().first {
                        it.composition.name != initialProductName
                    }

                assertEquals(false, updatedEntry.composition.name == initialProductName)
            }
        }

    @Test
    fun updating_user_recipe_servings_updates_diary_entries_using_it_by_serving() = runTest {
        runKoin {
            // 1. Create Child Recipe with 100g total
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components =
                        listOf(
                            MeasuredFoodSnapshot(
                                snapshot =
                                    AnonymousFoodSnapshot(
                                        id = FoodSnapshotId.Anonymous(Uuid.random()),
                                        name =
                                            FoodName(
                                                english = "Ingredient",
                                                fallback = "Ingredient",
                                            ),
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
                        ),
                )

            // 2. Create Diary Entry using 2 servings of Recipe
            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        CompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.id),
                            name = FoodName(english = "Recipe", fallback = "Recipe"),
                            brand = null,
                            image = null,
                            components = emptyList(),
                        ),
                    quantity =
                        FoodSnapshotQuantity.Serving(
                            servings = 2.0,
                            servingWeight = 100.grams,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.id))
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(200.grams, entry.composition.quantity.absoluteWeight)

            // 3. Update Recipe to have 2 servings instead of 1.
            // New serving weight should be 100g / 2 = 50g.
            userRecipeService.edit(
                identity = recipeId,
                name = FoodName(english = "Recipe", fallback = "Recipe"),
                note = "Updated",
                imageBytes = null,
                servings = 2.0,
                components =
                    listOf(
                        MeasuredFoodSnapshot(
                            snapshot =
                                AnonymousFoodSnapshot(
                                    id = FoodSnapshotId.Anonymous(Uuid.random()),
                                    name =
                                        FoodName(english = "Ingredient", fallback = "Ingredient"),
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
                    ),
            )

            // 4. Verify Diary Entry is updated.
            // Entry used 2 servings. Now 2 * 50g = 100g.
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.composition.quantity.absoluteWeight == 100.grams
                }

            val updatedComponent = updatedEntry.composition.snapshot as CompositeFoodSnapshot
            assertEquals(50.grams, updatedEntry.composition.quantity.servingWeight)
        }
    }

    @Test
    fun deleting_user_recipe_removes_it_from_diary_entries() = runTest {
        runKoin {
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        CompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.id),
                            name = FoodName(english = "To Delete", fallback = "To Delete"),
                            brand = null,
                            image = null,
                            components = emptyList(),
                        ),
                    quantity =
                        FoodSnapshotQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.id))
            foodDiaryService.observe(entryId).filterNotNull().first()

            userRecipeService.delete(recipeId, DeleteStrategy.Delete)

            // Verify the diary entry has been deleted
            val deletedEntry = foodDiaryService.observe(entryId).first { it == null }
            assertEquals(null, deletedEntry)
        }
    }

    @Test
    fun unlinking_user_recipe_anonymizes_it_in_diary_entries() = runTest {
        runKoin {
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            val name = FoodName(english = "To Unlink", fallback = "To Unlink")
            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        CompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.id),
                            name = name,
                            brand = null,
                            image = null,
                            components = emptyList(),
                        ),
                    quantity =
                        FoodSnapshotQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.id))
            foodDiaryService.observe(entryId).filterNotNull().first()

            userRecipeService.delete(recipeId, DeleteStrategy.Unlink)

            // Verify the diary entry has been anonymized
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.composition.identity is FoodSnapshotId.Anonymous
                }

            assertIs<FoodSnapshotId.Anonymous>(updatedEntry.composition.identity)
            assertEquals(name, updatedEntry.composition.name)
        }
    }

    private suspend fun Koin.waitForComposition(
        entryId: FoodDiaryEntryIdentity,
        componentId: FoodSnapshotId.Tracked,
    ) {
        val repository = get<FoodDiaryCompositionRepository>()
        withContext(Dispatchers.Default) {
            while (!repository.findEntriesUsing(componentId).contains(entryId)) {
                delay(10.milliseconds)
            }
        }
    }

    private val Koin.foodDiaryService: FoodDiaryService
        get() = get()

    private val Koin.userRecipeService: UserRecipeService
        get() = get()

    private val Koin.userProductService: UserProductService
        get() = get()

    private val testModule = module {
        single { TestOpenFoodFactsSettingsRepository(OpenFoodFactsSettings(remoteEnabled = true)) }
            .bind<OpenFoodFactsSettingsRepository>()
        single {
            val apiKey = TestSecrets.usdaApiKey
            TestFoodDataCentralSettingsRepository(
                FoodDataCentralSettings(
                    remoteEnabled = true,
                    apiKey = apiKey?.let { SoftwareEncrypted.encryptString(it) },
                )
            )
        }
            .bind<FoodDataCentralSettingsRepository>()
    }
}
