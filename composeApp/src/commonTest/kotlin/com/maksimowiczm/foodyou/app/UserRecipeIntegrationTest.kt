package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
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
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlin.test.Test
import kotlin.test.assertEquals
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
            val components =
                listOf(
                    FoodCompositionComponent.Simple(
                        identity = FoodCompositionComponentIdentity.UserProduct(productId.id),
                        name = initialProductName,
                        image = null,
                        nutritionFacts = initialNutrition,
                        quantity =
                            FoodComponentComponentQuantity.Weight(
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
                    components = components,
                )

            // 3. Wait until recipe is created
            userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodCompositionComponentIdentity.UserProduct(productId.id))

            // 4. Update User Product
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

            // 5. Verify the recipe has been updated
            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.components.first().name == updatedProductName
                }

            val component = updatedRecipe.components.first() as FoodCompositionComponent.Simple
            assertEquals(updatedProductName, component.name)
            assertEquals(updatedNutrition, component.nutritionFacts)
        }
    }

    @Test
    fun updating_user_product_serving_weight_updates_recipes_using_it_by_serving() = runTest {
        runKoin(testModule) {
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

            // 2. Create a Recipe using 2 servings of this product
            val components =
                listOf(
                    FoodCompositionComponent.Simple(
                        identity = FoodCompositionComponentIdentity.UserProduct(productId.id),
                        name = FoodName(english = "Product", fallback = "Product"),
                        image = null,
                        nutritionFacts = NutritionFacts(),
                        quantity =
                            FoodComponentComponentQuantity.Serving(
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
                    imageBytes = null,
                    servings = 1.0,
                    components = components,
                )

            // 3. Wait until recipe is created and verify initial absolute weight (2 * 30g = 60g)
            val recipe = userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodCompositionComponentIdentity.UserProduct(productId.id))
            assertEquals(60.grams, recipe.totalWeight)

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

            // 5. Verify the recipe has been updated and absolute weight is now 80g (2 * 40g)
            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.totalWeight == 80.grams
                }

            val component = updatedRecipe.components.first() as FoodCompositionComponent.Simple
            assertEquals(40.grams, component.quantity.servingWeight)
            assertEquals(80.grams, component.quantity.absoluteWeight)
        }
    }

    @Test
    fun updating_open_food_facts_product_updates_recipes_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val barcode = "8000500179864"
                val identity = OpenFoodFactsProductIdentity(barcode)

                // 1. Create a Recipe using an OFF product (will be initially empty or minimal)
                val initialProductName = FoodName(english = "OFF Product", fallback = "OFF Product")

                val components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.OpenFoodFacts(barcode),
                            name = initialProductName,
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
                val recipeId =
                    userRecipeService.create(
                        name = FoodName(english = "Recipe", fallback = "Recipe"),
                        note = null,
                        imageBytes = null,
                        servings = 1.0,
                        components = components,
                    )

                // 2. Wait until recipe is created
                userRecipeService.observe(recipeId).filterNotNull().first()
                waitForComposition(
                    recipeId,
                    FoodCompositionComponentIdentity.OpenFoodFacts(barcode),
                )

                // 3. Trigger OFF refresh (this will fetch real data if internet is available, or
                // fail
                // if
                // not)
                // If it fails due to network, we might need a different approach, but "real
                // integration"
                // implies network.
                get<OpenFoodFactsService>().refresh(identity).expect("Refreshed OFF product")

                // 4. Wait for synchronizer to update the recipe
                // Since we don't know the real name of Nutella in all languages, we just wait for
                // ANY
                // change in name
                val updatedRecipe =
                    userRecipeService.observe(recipeId).filterNotNull().first {
                        it.components.first().name != initialProductName
                    }

                val component = updatedRecipe.components.first() as FoodCompositionComponent.Simple
                // Verify that the name is no longer the fallback
                assertEquals(false, component.name == initialProductName)
            }
        }

    @Test
    fun updating_food_data_central_product_updates_recipes_using_it() =
        runTest(timeout = 30.seconds) {
            runKoin(testModule) {
                val fdcId = 2768188
                val identity = FoodDataCentralProductIdentity(fdcId)

                // 1. Create a Recipe using an FDC product
                val initialProductName = FoodName(english = "FDC Product", fallback = "FDC Product")

                val components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.FoodDataCentral(fdcId),
                            name = initialProductName,
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
                val recipeId =
                    userRecipeService.create(
                        name = FoodName(english = "Recipe", fallback = "Recipe"),
                        note = null,
                        imageBytes = null,
                        servings = 1.0,
                        components = components,
                    )

                // 2. Wait until recipe is created
                userRecipeService.observe(recipeId).filterNotNull().first()
                waitForComposition(
                    recipeId,
                    FoodCompositionComponentIdentity.FoodDataCentral(fdcId),
                )

                // 3. Trigger FDC refresh
                get<FoodDataCentralService>().refresh(identity).expect("Refreshed FDC product")

                // 4. Wait for synchronizer to update the recipe
                val updatedRecipe =
                    userRecipeService.observe(recipeId).filterNotNull().first {
                        it.components.first().name != initialProductName
                    }

                val component = updatedRecipe.components.first() as FoodCompositionComponent.Simple
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
                    imageBytes = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            // 2. Create Parent Recipe using Child Recipe
            val parentComponents =
                listOf(
                    FoodCompositionComponent.Composite(
                        identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                        name = childInitialName,
                        image = null,
                        quantity =
                            FoodComponentComponentQuantity.Weight(
                                absoluteWeight = 100.grams,
                                servingWeight = null,
                                packageWeight = null,
                            ),
                        components = emptyList(),
                    )
                )
            val parentId =
                userRecipeService.create(
                    name = FoodName(english = "Parent Recipe", fallback = "Parent Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components = parentComponents,
                )

            // Wait until recipe is created
            userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodCompositionComponentIdentity.Recipe(childId.id))

            // 3. Update Child Recipe
            val childUpdatedName =
                FoodName(english = "Updated Child Recipe", fallback = "Updated Child Recipe")

            userRecipeService.edit(
                identity = childId,
                name = childUpdatedName,
                note = "Updated",
                imageBytes = null,
                servings = 1.0,
                components = emptyList(),
            )

            // 4. Verify Parent Recipe is updated
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.components.first().name == childUpdatedName
                }

            val component = updatedParent.components.first() as FoodCompositionComponent.Composite
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
                    imageBytes = null,
                    servings = 1.0,
                    components =
                        listOf(
                            FoodCompositionComponent.Anonymous(
                                identity =
                                    FoodCompositionComponentIdentity.Anonymous(Uuid.random()),
                                name = FoodName(english = "Ingredient", fallback = "Ingredient"),
                                image = null,
                                nutritionFacts = NutritionFacts(),
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
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
                    imageBytes = null,
                    servings = 1.0,
                    components =
                        listOf(
                            FoodCompositionComponent.Composite(
                                identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                                name = FoodName(english = "Child", fallback = "Child"),
                                image = null,
                                quantity =
                                    FoodComponentComponentQuantity.Serving(
                                        servings = 2.0,
                                        servingWeight = 100.grams,
                                        packageWeight = null,
                                    ),
                                components = emptyList(),
                            )
                        ),
                )

            // Wait until recipe is created. Total weight should be 200g
            val parent = userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodCompositionComponentIdentity.Recipe(childId.id))
            assertEquals(200.grams, parent.totalWeight)

            // 3. Update Child Recipe to have 2 servings instead of 1.
            // New serving weight should be 100g / 2 = 50g.
            userRecipeService.edit(
                identity = childId,
                name = FoodName(english = "Child", fallback = "Child"),
                note = "Updated",
                imageBytes = null,
                servings = 2.0,
                components =
                    listOf(
                        FoodCompositionComponent.Anonymous(
                            identity = FoodCompositionComponentIdentity.Anonymous(Uuid.random()),
                            name = FoodName(english = "Ingredient", fallback = "Ingredient"),
                            image = null,
                            nutritionFacts = NutritionFacts(),
                            quantity =
                                FoodComponentComponentQuantity.Weight(
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

            val component = updatedParent.components.first() as FoodCompositionComponent.Composite
            assertEquals(50.grams, component.quantity.servingWeight)
        }
    }

    @Test
    fun deleting_user_product_removes_it_from_recipes() = runTest {
        runKoin(testModule) {
            val productId =
                userProductService.create(
                    name = FoodName(english = "Product", fallback = "Product"),
                    brand = "Brand",
                    barcode = UserProductBarcode("123456789"),
                    note = null,
                    imageBytes = null,
                    servingQuantity = null,
                    packageQuantity = null,
                    isLiquid = false,
                    nutritionFacts = NutritionFacts(),
                )

            val otherIngredientId = FoodCompositionComponentIdentity.OpenFoodFacts("other")
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components =
                        listOf(
                            FoodCompositionComponent.Simple(
                                identity =
                                    FoodCompositionComponentIdentity.UserProduct(productId.id),
                                name = FoodName(english = "To Delete", fallback = "To Delete"),
                                image = null,
                                nutritionFacts = NutritionFacts(),
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
                                        absoluteWeight = 100.grams,
                                        servingWeight = null,
                                        packageWeight = null,
                                    ),
                            ),
                            FoodCompositionComponent.Simple(
                                identity = otherIngredientId,
                                name = FoodName(english = "Keep Me", fallback = "Keep Me"),
                                image = null,
                                nutritionFacts = NutritionFacts(),
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
                                        absoluteWeight = 100.grams,
                                        servingWeight = null,
                                        packageWeight = null,
                                    ),
                            ),
                        ),
                )
            // Wait until recipe is created
            userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodCompositionComponentIdentity.UserProduct(productId.id))

            userProductService.delete(productId, DeleteStrategy.Delete)

            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.components.size == 1
                }

            assertEquals(1, updatedRecipe.components.size)
            assertEquals(otherIngredientId, updatedRecipe.components.first().identity)
        }
    }

    @Test
    fun unlinking_user_product_anonymizes_it_in_recipes() = runTest {
        runKoin(testModule) {
            val productId =
                userProductService.create(
                    name = FoodName(english = "Product", fallback = "Product"),
                    brand = "Brand",
                    barcode = UserProductBarcode("123456789"),
                    note = null,
                    imageBytes = null,
                    servingQuantity = null,
                    packageQuantity = null,
                    isLiquid = false,
                    nutritionFacts = NutritionFacts(),
                )

            val name = FoodName(english = "To Unlink", fallback = "To Unlink")

            val nutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
            val recipeId =
                userRecipeService.create(
                    name = FoodName(english = "Recipe", fallback = "Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components =
                        listOf(
                            FoodCompositionComponent.Simple(
                                identity =
                                    FoodCompositionComponentIdentity.UserProduct(productId.id),
                                name = name,
                                image = null,
                                nutritionFacts = nutrition,
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
                                        absoluteWeight = 100.grams,
                                        servingWeight = null,
                                        packageWeight = null,
                                    ),
                            )
                        ),
                )

            // Wait until recipe is created
            userRecipeService.observe(recipeId).filterNotNull().first()
            waitForComposition(recipeId, FoodCompositionComponentIdentity.UserProduct(productId.id))

            userProductService.delete(productId, DeleteStrategy.Unlink)

            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.components.first().identity is FoodCompositionComponentIdentity.Anonymous
                }

            assertEquals(1, updatedRecipe.components.size)
            val component = updatedRecipe.components.first()
            assertEquals(true, component.identity is FoodCompositionComponentIdentity.Anonymous)
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
                    imageBytes = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            // 2. Create Parent Recipe with 2 ingredients (one is the child recipe)
            val otherIngredientId = FoodCompositionComponentIdentity.OpenFoodFacts("other")
            val parentId =
                userRecipeService.create(
                    name = FoodName(english = "Parent", fallback = "Parent"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components =
                        listOf(
                            FoodCompositionComponent.Composite(
                                identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                                name = FoodName(english = "Child", fallback = "Child"),
                                image = null,
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
                                        absoluteWeight = 100.grams,
                                        servingWeight = null,
                                        packageWeight = null,
                                    ),
                                components = emptyList(),
                            ),
                            FoodCompositionComponent.Simple(
                                identity = otherIngredientId,
                                name = FoodName(english = "Other", fallback = "Other"),
                                image = null,
                                nutritionFacts = NutritionFacts(),
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
                                        absoluteWeight = 100.grams,
                                        servingWeight = null,
                                        packageWeight = null,
                                    ),
                            ),
                        ),
                )

            // Wait until recipe is created
            userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodCompositionComponentIdentity.Recipe(childId.id))

            // 3. Delete Child Recipe
            userRecipeService.delete(childId, DeleteStrategy.Delete)

            // 4. Verify Parent Recipe has one less ingredient
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.components.size == 1
                }

            assertEquals(1, updatedParent.components.size)
            assertEquals(otherIngredientId, updatedParent.components.first().identity)
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
                    imageBytes = null,
                    servings = 1.0,
                    components = emptyList(),
                )

            // 2. Create Parent Recipe
            val parentId =
                userRecipeService.create(
                    name = FoodName(english = "Parent", fallback = "Parent"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    components =
                        listOf(
                            FoodCompositionComponent.Composite(
                                identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                                name = FoodName(english = "Child", fallback = "Child"),
                                image = null,
                                quantity =
                                    FoodComponentComponentQuantity.Weight(
                                        absoluteWeight = 100.grams,
                                        servingWeight = null,
                                        packageWeight = null,
                                    ),
                                components = emptyList(),
                            )
                        ),
                )

            // Wait until recipe is created
            userRecipeService.observe(parentId).filterNotNull().first()
            waitForComposition(parentId, FoodCompositionComponentIdentity.Recipe(childId.id))

            // 3. Unlink Child Recipe
            userRecipeService.delete(childId, DeleteStrategy.Unlink)

            // 4. Verify Parent Recipe has anonymous ingredient
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.components.first().identity is FoodCompositionComponentIdentity.Anonymous
                }

            assertEquals(1, updatedParent.components.size)
            assertEquals(
                true,
                updatedParent.components.first().identity
                    is FoodCompositionComponentIdentity.Anonymous,
            )
        }
    }

    private suspend fun Koin.waitForComposition(
        recipeId: UserRecipeIdentity,
        componentId: FoodCompositionComponentIdentity.Identified,
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
