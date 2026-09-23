package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
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
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
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

class UserRecipeIntegrationTest {
    @Test
    fun updating_user_product_updates_recipes_using_it() = runTest {
        runKoin(testModule) {
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
                            )
                    ),
            )

            // 2. Create a Recipe using this product
            val components =
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
                    components = components,
                )

            // 3. Wait until recipe is created
            userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodSnapshotId.UserProduct(productId.value))

            // 4. Update User Product
            val updatedProductName =
                FoodName(english = "Updated Product", fallback = "Updated Product")

            val updatedNutrition = NutritionFacts(proteins = NutrientValue.Complete(20.grams))
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Update {
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

            // 5. Verify the recipe has been updated
            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.components.first().name == updatedProductName
                }

            val component = updatedRecipe.components.first().snapshot as TrackedLeafFoodSnapshot
            assertEquals(updatedProductName, component.name)
            assertEquals(updatedNutrition, component.nutritionFacts)
        }
    }

    @Test
    fun updating_user_product_serving_weight_updates_recipes_using_it_by_serving() = runTest {
        runKoin(testModule) {
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
                            )
                    ),
            )

            // 2. Create a Recipe using 2 servings of this product
            val components =
                listOf(
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
                )
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components = components,
                )

            // 3. Wait until recipe is created and verify initial absolute weight (2 * 30g = 60g)
            val recipe = userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodSnapshotId.UserProduct(productId.value))
            assertEquals(60.grams, recipe.totalWeight)

            // 4. Update product serving weight to 40g
            userProductService.handle(
                id = productId,
                command =
                    UserProductCommand.Update {
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

            // 5. Verify the recipe has been updated and absolute weight is now 80g (2 * 40g)
            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.totalWeight == 80.grams
                }

            val component = updatedRecipe.components.first()
            assertEquals(40.grams, component.quantity.servingWeight)
            assertEquals(80.grams, component.quantity.absoluteWeight)
        }
    }

    @Test
    fun updating_open_food_facts_product_updates_recipes_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val barcode = "8000500179864"
                val id = OpenFoodFactsProductId(barcode)

                // 1. Create a Recipe using an OFF product (will be initially empty or minimal)
                val initialProductName = FoodName(english = "OFF Product", fallback = "OFF Product")

                val components =
                    listOf(
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
                    )
                val recipeId =
                    userRecipeService.create(
                        name = FoodName(english = "Recipe", fallback = "Recipe"),
                        note = null,
                        image = null,
                        servings = 1.0,
                        components = components,
                    )

                // 2. Wait until recipe is created
                userRecipeService.observe(recipeId).filterNotNull().first()
                waitForComposition(
                    recipeId,
                    FoodSnapshotId.OpenFoodFacts(barcode),
                )

                // 3. Trigger OFF refresh (this will fetch real data if internet is available, or
                // fail
                // if
                // not)
                // If it fails due to network, we might need a different approach, but "real
                // integration"
                // implies network.
                get<OpenFoodFactsService>().refresh(id).expect("Refreshed OFF product")

                // 4. Wait for synchronizer to update the recipe
                // Since we don't know the real name of Nutella in all languages, we just wait for
                // ANY
                // change in name
                val updatedRecipe =
                    userRecipeService.observe(recipeId).filterNotNull().first {
                        it.components.first().name != initialProductName
                    }

                val component = updatedRecipe.components.first().snapshot as TrackedLeafFoodSnapshot
                // Verify that the name is no longer the fallback
                assertEquals(false, component.name == initialProductName)
            }
        }

    @Test
    fun updating_food_data_central_product_updates_recipes_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val fdcId = 2768188
                val id = FoodDataCentralProductId(fdcId)

                // 1. Create a Recipe using an FDC product
                val initialProductName = FoodName(english = "FDC Product", fallback = "FDC Product")

                val components =
                    listOf(
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
                    )
                val recipeId =
                    userRecipeService.create(
                        name = FoodName(english = "Recipe", fallback = "Recipe"),
                        note = null,
                        image = null,
                        servings = 1.0,
                        components = components,
                    )

                // 2. Wait until recipe is created
                userRecipeService.observe(recipeId).filterNotNull().first()
                waitForComposition(
                    recipeId,
                    FoodSnapshotId.FoodDataCentral(fdcId),
                )

                // 3. Trigger FDC refresh
                get<FoodDataCentralService>().refresh(id).expect("Refreshed FDC product")

                // 4. Wait for synchronizer to update the recipe
                val updatedRecipe =
                    userRecipeService.observe(recipeId).filterNotNull().first {
                        it.components.first().name != initialProductName
                    }

                val component = updatedRecipe.components.first().snapshot as TrackedLeafFoodSnapshot
                assertEquals(false, component.name == initialProductName)
            }
        }

    @Test
    fun updating_nested_recipe_updates_parent_recipe() = runTest {
        runKoin(testModule) {
            // 1. Create Child Recipe
            val childInitialName = FoodName(english = "Child Recipe", fallback = "Child Recipe")

            val childId =
                userRecipeService.create(
                    name = childInitialName,
                    note = null,
                    image = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            // 2. Create Parent Recipe using Child Recipe
            val parentComponents =
                listOf(
                    MeasuredFoodSnapshot(
                        snapshot =
                            TrackedCompositeFoodSnapshot(
                                id = FoodSnapshotId.UserRecipe(childId.value),
                                name = childInitialName,
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
                )
            val parentId =
                userRecipeService.create(
                    name = FoodName(english = "Parent Recipe", fallback = "Parent Recipe"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components = parentComponents,
                )

            // Wait until recipe is created
            userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodSnapshotId.UserRecipe(childId.value))

            // 3. Update Child Recipe
            val childUpdatedName =
                FoodName(english = "Updated Child Recipe", fallback = "Updated Child Recipe")

            userRecipeService.edit(
                id = childId,
                name = childUpdatedName,
                note = "Updated",
                image = null,
                servings = 1.0,
                components = emptyList(),
            )

            // 4. Verify Parent Recipe is updated
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.components.first().name == childUpdatedName
                }

            val component = updatedParent.components.first().snapshot as CompositeFoodSnapshot
            assertEquals(childUpdatedName, component.name)
        }
    }

    @Test
    fun updating_nested_recipe_servings_updates_parent_recipe_using_it_by_serving() = runTest {
        runKoin(testModule) {
            // 1. Create Child Recipe with 100g total
            val childId =
                userRecipeService.create(
                    name = FoodName(english = "Child", fallback = "Child"),
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

            // 2. Create Parent Recipe using 2 servings of Child Recipe
            val parentId =
                userRecipeService.create(
                    name = FoodName(english = "Parent", fallback = "Parent"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components =
                        listOf(
                            MeasuredFoodSnapshot(
                                snapshot =
                                    TrackedCompositeFoodSnapshot(
                                        id = FoodSnapshotId.UserRecipe(childId.value),
                                        name = FoodName(english = "Child", fallback = "Child"),
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
                        ),
                )

            // Wait until recipe is created. Total weight should be 200g
            val parent = userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodSnapshotId.UserRecipe(childId.value))
            assertEquals(200.grams, parent.totalWeight)

            // 3. Update Child Recipe to have 2 servings instead of 1.
            // New serving weight should be 100g / 2 = 50g.
            userRecipeService.edit(
                id = childId,
                name = FoodName(english = "Child", fallback = "Child"),
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

            // 4. Verify Parent Recipe is updated.
            // Parent used 2 servings. Now 2 * 50g = 100g.
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.totalWeight == 100.grams
                }

            val component = updatedParent.components.first()
            assertEquals(50.grams, component.quantity.servingWeight)
        }
    }

    @Test
    fun deleting_user_product_removes_it_from_recipes() = runTest {
        runKoin(testModule) {
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
                                barcode = UserProductBarcode("123456789"),
                                note = null,
                                image = null,
                                servingQuantity = null,
                                packageQuantity = null,
                                isLiquid = false,
                                nutritionFacts = NutritionFacts(),
                            )
                    ),
            )

            val otherIngredientId = FoodSnapshotId.OpenFoodFacts("other")
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
                                    TrackedLeafFoodSnapshot(
                                        id = FoodSnapshotId.UserProduct(productId.value),
                                        name =
                                            FoodName(english = "To Delete", fallback = "To Delete"),
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
                            ),
                            MeasuredFoodSnapshot(
                                snapshot =
                                    TrackedLeafFoodSnapshot(
                                        id = otherIngredientId,
                                        name = FoodName(english = "Keep Me", fallback = "Keep Me"),
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
                            ),
                        ),
                )
            // Wait until recipe is created
            userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodSnapshotId.UserProduct(productId.value))

            userProductService.handle(
                id = productId,
                command = UserProductCommand.Remove(strategy = DeleteStrategy.Delete),
            )

            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.components.size == 1
                }

            assertEquals(1, updatedRecipe.components.size)
            assertEquals(otherIngredientId, updatedRecipe.components.first().id)
        }
    }

    @Test
    fun unlinking_user_product_anonymizes_it_in_recipes() = runTest {
        runKoin(testModule) {
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
                                barcode = UserProductBarcode("123456789"),
                                note = null,
                                image = null,
                                servingQuantity = null,
                                packageQuantity = null,
                                isLiquid = false,
                                nutritionFacts = NutritionFacts(),
                            )
                    ),
            )

            val name = FoodName(english = "To Unlink", fallback = "To Unlink")

            val nutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
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
                                    TrackedLeafFoodSnapshot(
                                        id = FoodSnapshotId.UserProduct(productId.value),
                                        name = name,
                                        brand = null,
                                        image = null,
                                        nutritionFacts = nutrition,
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

            // Wait until recipe is created
            userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodSnapshotId.UserProduct(productId.value))

            userProductService.handle(
                id = productId,
                command = UserProductCommand.Remove(strategy = DeleteStrategy.Unlink),
            )

            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.components.first().id is FoodSnapshotId.Anonymous
                }

            assertEquals(1, updatedRecipe.components.size)
            val component = updatedRecipe.components.first()
            val anonymousId = assertIs<FoodSnapshotId.Anonymous>(component.id)
            assertEquals(FoodSnapshotId.UserProduct(productId.value), anonymousId.trackedId)
            assertEquals(name, component.name)
            assertEquals(nutrition, component.nutritionFacts)
        }
    }

    @Test
    fun deleting_nested_recipe_removes_it_from_parent_recipe() = runTest {
        runKoin(testModule) {
            // 1. Create Child Recipe
            val childId =
                userRecipeService.create(
                    name = FoodName(english = "Child", fallback = "Child"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            // 2. Create Parent Recipe with 2 ingredients (one is the child recipe)
            val otherIngredientId = FoodSnapshotId.OpenFoodFacts("other")
            val parentId =
                userRecipeService.create(
                    name = FoodName(english = "Parent", fallback = "Parent"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components =
                        listOf(
                            MeasuredFoodSnapshot(
                                snapshot =
                                    TrackedCompositeFoodSnapshot(
                                        id = FoodSnapshotId.UserRecipe(childId.value),
                                        name = FoodName(english = "Child", fallback = "Child"),
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
                            ),
                            MeasuredFoodSnapshot(
                                snapshot =
                                    TrackedLeafFoodSnapshot(
                                        id = otherIngredientId,
                                        name = FoodName(english = "Other", fallback = "Other"),
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
                            ),
                        ),
                )

            // Wait until recipe is created
            userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodSnapshotId.UserRecipe(childId.value))

            // 3. Delete Child Recipe
            userRecipeService.delete(childId, DeleteStrategy.Delete)

            // 4. Verify Parent Recipe has one less ingredient
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.components.size == 1
                }

            assertEquals(1, updatedParent.components.size)
            assertEquals(otherIngredientId, updatedParent.components.first().id)
        }
    }

    @Test
    fun unlinking_nested_recipe_anonymizes_it_in_parent_recipe() = runTest {
        runKoin(testModule) {
            // 1. Create Child Recipe
            val childId =
                userRecipeService.create(
                    name = FoodName(english = "Child", fallback = "Child"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            // 2. Create Parent Recipe
            val parentId =
                userRecipeService.create(
                    name = FoodName(english = "Parent", fallback = "Parent"),
                    note = null,
                    image = null,
                    servings = 1.0,
                    components =
                        listOf(
                            MeasuredFoodSnapshot(
                                snapshot =
                                    TrackedCompositeFoodSnapshot(
                                        id = FoodSnapshotId.UserRecipe(childId.value),
                                        name = FoodName(english = "Child", fallback = "Child"),
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
                        ),
                )

            // Wait until recipe is created
            userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodSnapshotId.UserRecipe(childId.value))

            // 3. Unlink Child Recipe
            userRecipeService.delete(childId, DeleteStrategy.Unlink)

            // 4. Verify Parent Recipe has anonymous ingredient
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.components.first().id is FoodSnapshotId.Anonymous
                }

            assertEquals(1, updatedParent.components.size)
            val anonymousId =
                assertIs<FoodSnapshotId.Anonymous>(updatedParent.components.first().id)
            assertEquals(FoodSnapshotId.UserRecipe(childId.value), anonymousId.trackedId)
        }
    }

    private suspend fun Koin.waitForComposition(
        recipeId: UserRecipeId,
        componentId: FoodSnapshotId.Tracked,
    ) {
        val repository = get<UserRecipeCompositionRepository>()
        withContext(Dispatchers.Default) {
            while (!repository.findRecipesUsing(componentId).contains(recipeId)) {
                delay(10.milliseconds)
            }
        }
    }

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
