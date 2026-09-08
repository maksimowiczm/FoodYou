package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity.Weight
import com.maksimowiczm.foodyou.common.domain.food.AnonymousLeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.TrackedCompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.TrackedLeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.expect
import com.maksimowiczm.foodyou.common.infrastructure.crypto.SoftwareEncrypted
import com.maksimowiczm.foodyou.common.infrastructure.crypto.encryptString
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettings
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCommand
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
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
    private val mealId = MealId()

    @Test
    fun updating_user_product_updates_diary_entries_using_it() = runTest {
        runKoin {
            // 1. Create a User Product
            val initialProductName =
                FoodName(english = "Initial Product", fallback = "Initial Product")
            val initialNutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
            val productId = UserProductId()
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Create(
                        product =
                            UserProduct(
                                id = productId,
                                name = initialProductName,
                                brand = "Brand",
                                barcode = null,
                                note = null,
                                image = null,
                                nutritionFacts = initialNutrition,
                                servingQuantity = null,
                                packageQuantity = null,
                                isLiquid = false,
                            ),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 2. Create a Diary Entry using this product
            val snapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedLeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.value))
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(initialProductName, entry.snapshot.name)

            // 4. Update the User Product
            val updatedProductName =
                FoodName(english = "Updated Product", fallback = "Updated Product")
            val updatedNutrition = NutritionFacts(proteins = NutrientValue.Complete(20.grams))

            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Update(timestamp = Clock.System.now()) {
                        it.copy(
                            name = updatedProductName,
                            brand = "Brand",
                            barcode = null,
                            note = null,
                            image = null,
                            nutritionFacts = updatedNutrition,
                            servingQuantity = null,
                            packageQuantity = null,
                            isLiquid = false,
                        )
                    },
            )

            // 5. Verify the diary entry has been updated
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.snapshot.name == updatedProductName
                }

            val updatedComponent = updatedEntry.snapshot.snapshot as TrackedLeafFoodSnapshot
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
                            TrackedLeafFoodSnapshot(
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
                    image = null,
                    servings = 1.0,
                    components = recipeComponents,
                )

            // 2. Create a Diary Entry using this recipe
            val entrySnapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedCompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = entrySnapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.value))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 4. Update the Recipe
            val updatedRecipeName =
                FoodName(english = "Updated Recipe", fallback = "Updated Recipe")
            userRecipeService.edit(
                id = recipeId,
                name = updatedRecipeName,
                note = "Updated",
                image = null,
                servings = 1.0,
                components = recipeComponents,
            )

            // 5. Verify the diary entry has been updated
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.snapshot.name == updatedRecipeName
                }

            val updatedComponent = updatedEntry.snapshot.snapshot as CompositeFoodSnapshot
            assertEquals(updatedRecipeName, updatedComponent.name)
        }
    }

    @Test
    fun updating_user_product_used_in_recipe_updates_diary_entry() = runTest {
        runKoin {
            // 1. Create a User Product
            val initialProductName = FoodName(english = "Product", fallback = "Product")
            val initialNutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
            val productId = UserProductId()
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Create(
                        product =
                            UserProduct(
                                id = productId,
                                name = initialProductName,
                                brand = "Brand",
                                barcode = null,
                                note = null,
                                image = null,
                                nutritionFacts = initialNutrition,
                                servingQuantity = null,
                                packageQuantity = null,
                                isLiquid = false,
                            ),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 2. Create a Recipe using this product
            val recipeComponents =
                listOf(
                    MeasuredFoodSnapshot(
                        snapshot =
                            TrackedLeafFoodSnapshot(
                                id = FoodSnapshotId.UserProduct(productId.value),
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
                    image = null,
                    servings = 1.0,
                    components = recipeComponents,
                )

            // 3. Create a Diary Entry using this recipe
            val entrySnapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedCompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = entrySnapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 4. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.value))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 5. Update the User Product
            val updatedProductName =
                FoodName(english = "Updated Product", fallback = "Updated Product")
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Update(timestamp = Clock.System.now()) {
                        it.copy(
                            name = updatedProductName,
                            brand = "Brand",
                            barcode = null,
                            note = null,
                            image = null,
                            nutritionFacts = initialNutrition,
                            servingQuantity = null,
                            packageQuantity = null,
                            isLiquid = false,
                        )
                    },
            )

            // 6. Verify the diary entry (and the nested recipe) has been updated
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first { entry ->
                    val composite = entry.snapshot.snapshot as CompositeFoodSnapshot
                    composite.components.first().name == updatedProductName
                }

            val composite = updatedEntry.snapshot.snapshot as CompositeFoodSnapshot
            val simple = composite.components.first().snapshot as TrackedLeafFoodSnapshot
            assertEquals(updatedProductName, simple.name)
        }
    }

    @Test
    fun deleting_user_product_removes_it_from_diary_entries() = runTest {
        runKoin {
            // 1. Create a User Product
            val productId = UserProductId()
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Create(
                        product =
                            UserProduct(
                                id = productId,
                                name = FoodName(english = "Product", fallback = "Product"),
                                brand = "Brand",
                                barcode = UserProductBarcode("123"),
                                note = null,
                                image = null,
                                servingQuantity = null,
                                packageQuantity = null,
                                isLiquid = false,
                                nutritionFacts = NutritionFacts(),
                            ),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 2. Create a Diary Entry using this product
            val snapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedLeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.value))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 4. Delete the User Product
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Remove(
                        strategy = DeleteStrategy.Delete,
                        timestamp = Clock.System.now(),
                    ),
            )

            // 5. Verify the diary entry has been deleted
            val deletedEntry = foodDiaryService.observe(entryId).first { it == null }
            assertEquals(null, deletedEntry)
        }
    }

    @Test
    fun unlinking_user_product_anonymizes_it_in_diary_entries() = runTest {
        runKoin {
            // 1. Create a User Product
            val productId = UserProductId()
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Create(
                        product =
                            UserProduct(
                                id = productId,
                                name = FoodName(english = "Product", fallback = "Product"),
                                brand = "Brand",
                                barcode = UserProductBarcode("123"),
                                note = null,
                                image = null,
                                servingQuantity = null,
                                packageQuantity = null,
                                isLiquid = false,
                                nutritionFacts = NutritionFacts(),
                            ),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 2. Create a Diary Entry using this product
            val snapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedLeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.value))
            foodDiaryService.observe(entryId).filterNotNull().first()

            // 4. Unlink the User Product
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Remove(
                        strategy = DeleteStrategy.Unlink,
                        timestamp = Clock.System.now(),
                    ),
            )

            // 5. Verify the diary entry has been anonymized
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.snapshot.id is FoodSnapshotId.Anonymous
                }

            val anonymousId = assertIs<FoodSnapshotId.Anonymous>(updatedEntry.snapshot.id)
            assertEquals(FoodSnapshotId.UserProduct(productId.value), anonymousId.trackedId)
        }
    }

    @Test
    fun updating_user_product_serving_weight_updates_diary_entries_using_it_by_serving() = runTest {
        runKoin {
            // 1. Create a User Product with initial serving weight
            val productId = UserProductId()
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Create(
                        product =
                            UserProduct(
                                id = productId,
                                name = FoodName(english = "Product", fallback = "Product"),
                                brand = "Brand",
                                barcode = null,
                                note = null,
                                image = null,
                                nutritionFacts = NutritionFacts(),
                                servingQuantity = Weight(30.grams),
                                packageQuantity = null,
                                isLiquid = false,
                            ),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 2. Create a Diary Entry using 2 servings of this product
            val snapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedLeafFoodSnapshot(
                            id = FoodSnapshotId.UserProduct(productId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // 3. Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserProduct(productId.value))
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(60.grams, entry.snapshot.quantity.absoluteWeight)

            // 4. Update product serving weight to 40g
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Update(timestamp = Clock.System.now()) {
                        it.copy(
                            name = FoodName(english = "Product", fallback = "Product"),
                            brand = "Brand",
                            barcode = null,
                            note = null,
                            image = null,
                            nutritionFacts = NutritionFacts(),
                            servingQuantity = Weight(40.grams),
                            packageQuantity = null,
                            isLiquid = false,
                        )
                    },
            )

            // 5. Verify the diary entry has been updated and absolute weight is now 80g (2 * 40g)
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.snapshot.quantity.absoluteWeight == 80.grams
                }

            assertEquals(40.grams, updatedEntry.snapshot.quantity.servingWeight)
        }
    }

    @Test
    fun updating_open_food_facts_product_updates_diary_entries_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val barcode = "8000500179864"
                val id = OpenFoodFactsProductId(barcode)

                // 1. Create a Diary Entry using an OFF product
                val initialProductName = FoodName(english = "OFF Product", fallback = "OFF Product")
                val snapshot =
                    MeasuredFoodSnapshot(
                        snapshot =
                            TrackedLeafFoodSnapshot(
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
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = snapshot,
                            mealId = mealId,
                            entryTimestamp = Clock.System.now(),
                            timestamp = Clock.System.now(),
                        ),
                )

                // 2. Wait until entry is created and indexed
                waitForComposition(entryId, FoodSnapshotId.OpenFoodFacts(barcode))
                foodDiaryService.observe(entryId).filterNotNull().first()

                // 3. Trigger OFF refresh
                get<OpenFoodFactsService>().refresh(id).expect("Refreshed OFF product")

                // 4. Wait for synchronizer to update the diary entry
                val updatedEntry =
                    foodDiaryService.observe(entryId).filterNotNull().first {
                        it.snapshot.name != initialProductName
                    }

                assertEquals(false, updatedEntry.snapshot.name == initialProductName)
            }
        }

    @Test
    fun updating_food_data_central_product_updates_diary_entries_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val fdcId = 2768188
                val id = FoodDataCentralProductId(fdcId)

                // 1. Create a Diary Entry using an FDC product
                val initialProductName = FoodName(english = "FDC Product", fallback = "FDC Product")
                val snapshot =
                    MeasuredFoodSnapshot(
                        snapshot =
                            TrackedLeafFoodSnapshot(
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
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = snapshot,
                            mealId = mealId,
                            entryTimestamp = Clock.System.now(),
                            timestamp = Clock.System.now(),
                        ),
                )

                // 2. Wait until entry is created and indexed
                waitForComposition(entryId, FoodSnapshotId.FoodDataCentral(fdcId))
                foodDiaryService.observe(entryId).filterNotNull().first()

                // 3. Trigger FDC refresh
                get<FoodDataCentralService>().refresh(id).expect("Refreshed FDC product")

                // 4. Wait for synchronizer to update the diary entry
                val updatedEntry =
                    foodDiaryService.observe(entryId).filterNotNull().first {
                        it.snapshot.name != initialProductName
                    }

                assertEquals(false, updatedEntry.snapshot.name == initialProductName)
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
                    image = null,
                    servings = 1.0,
                    components =
                        listOf(
                            MeasuredFoodSnapshot(
                                snapshot =
                                    AnonymousLeafFoodSnapshot(
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
            val snapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedCompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.value))
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(200.grams, entry.snapshot.quantity.absoluteWeight)

            // 3. Update Recipe to have 2 servings instead of 1.
            // New serving weight should be 100g / 2 = 50g.
            userRecipeService.edit(
                id = recipeId,
                name = FoodName(english = "Recipe", fallback = "Recipe"),
                note = "Updated",
                image = null,
                servings = 2.0,
                components =
                    listOf(
                        MeasuredFoodSnapshot(
                            snapshot =
                                AnonymousLeafFoodSnapshot(
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
                    it.snapshot.quantity.absoluteWeight == 100.grams
                }

            assertIs<CompositeFoodSnapshot>(updatedEntry.snapshot.snapshot)
            assertEquals(50.grams, updatedEntry.snapshot.quantity.servingWeight)
        }
    }

    @Test
    fun deleting_user_recipe_removes_it_from_diary_entries() = runTest {
        runKoin {
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            val snapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedCompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.value))
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
                    image = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            val name = FoodName(english = "To Unlink", fallback = "To Unlink")
            val snapshot =
                MeasuredFoodSnapshot(
                    snapshot =
                        TrackedCompositeFoodSnapshot(
                            id = FoodSnapshotId.UserRecipe(recipeId.value),
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
            val entryId = FoodDiaryEntryId()
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Create(
                        id = entryId,
                        profileIds = setOf(profileId),
                        snapshot = snapshot,
                        mealId = mealId,
                        entryTimestamp = Clock.System.now(),
                        timestamp = Clock.System.now(),
                    ),
            )

            // Wait until entry is created and indexed
            waitForComposition(entryId, FoodSnapshotId.UserRecipe(recipeId.value))
            foodDiaryService.observe(entryId).filterNotNull().first()

            userRecipeService.delete(recipeId, DeleteStrategy.Unlink)

            // Verify the diary entry has been anonymized
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.snapshot.id is FoodSnapshotId.Anonymous
                }

            val anonymousId = assertIs<FoodSnapshotId.Anonymous>(updatedEntry.snapshot.id)
            assertEquals(FoodSnapshotId.UserRecipe(recipeId.value), anonymousId.trackedId)
            assertEquals(name, updatedEntry.snapshot.name)
        }
    }

    private suspend fun Koin.waitForComposition(
        entryId: FoodDiaryEntryId,
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
