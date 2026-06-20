package com.maksimowiczm.foodyou.app

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.ReadModelDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.ReadModelDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomEventStore
import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodComposition
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.InMemoryEventBus
import com.maksimowiczm.foodyou.common.event.LoggingEventBus
import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.common.infrastructure.FileKitBlobStorage
import com.maksimowiczm.foodyou.common.infrastructure.provideRoomDatabaseBuilder
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductUpdatedEvent
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductUpdatedEvent
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductBarcode
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userproduct.domain.UserProductUpdatedEvent
import com.maksimowiczm.foodyou.userrecipe.application.CompositionSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.FoodDataCentralSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.NestedRecipeSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.OpenFoodFactsSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.UserProductSynchronizer
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.infrastructure.room.RoomUserRecipeCompositionRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.koin.core.Koin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class UserRecipeIntegrationTest {
    private lateinit var eventStoreDatabase: EventStoreDatabase
    private lateinit var readModelDatabase: ReadModelDatabase

    @BeforeTest
    fun setup() {
        eventStoreDatabase = provideRoomDatabaseBuilder<EventStoreDatabase>().buildDatabase()
        readModelDatabase = provideRoomDatabaseBuilder<ReadModelDatabase>().buildDatabase()
    }

    @AfterTest
    fun tearDown() {
        if (::eventStoreDatabase.isInitialized) eventStoreDatabase.close()
        if (::readModelDatabase.isInitialized) readModelDatabase.close()
    }

    @Test
    fun updating_user_product_updates_recipes_using_it() = runIntegrationTest {
        val userRecipeService = get<UserRecipeService>()
        val eventBus = get<EventBus>()

        // 1. Create a User Product
        val productId = UserProductIdentity(Uuid.random())
        val initialProductName = FoodName(fallback = "Initial Product")
        val initialNutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
        val product =
            UserProduct(
                identity = productId,
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

        // 2. Create a Recipe using this product
        val composition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.UserProduct(productId.id),
                            name = initialProductName,
                            image = null,
                            nutritionFacts = initialNutrition,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        )
                    )
            )
        val recipeId =
            userRecipeService.create(
                name = FoodName(fallback = "Recipe"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = composition,
            )

        // 3. Wait until recipe is created
        val recipe = userRecipeService.observe(recipeId).filterNotNull().first()
        assertEquals(
            initialProductName,
            (recipe.composition.components.first() as FoodCompositionComponent.Simple).name,
        )

        // 4. Simulate a User Product Update
        val updatedProductName = FoodName(fallback = "Updated Product")
        val updatedNutrition = NutritionFacts(proteins = NutrientValue.Complete(20.grams))
        val updatedProduct =
            product.copy(name = updatedProductName, nutritionFacts = updatedNutrition)

        eventBus.publish(
            UserProductUpdatedEvent(product = updatedProduct, timestamp = Clock.System.now())
        )

        // 5. Verify the recipe has been updated
        val updatedRecipe =
            userRecipeService.observe(recipeId).filterNotNull().first {
                it.composition.components.first().name == updatedProductName
            }

        val component =
            updatedRecipe.composition.components.first() as FoodCompositionComponent.Simple
        assertEquals(updatedProductName, component.name)
        assertEquals(updatedNutrition, component.nutritionFacts)
    }

    @Test
    fun updating_user_product_serving_weight_updates_recipes_using_it_by_serving() =
        runIntegrationTest {
            val userRecipeService = get<UserRecipeService>()
            val eventBus = get<EventBus>()

            // 1. Create a User Product with initial serving weight
            val productId = UserProductIdentity(Uuid.random())
            val product =
                UserProduct(
                    identity = productId,
                    name = FoodName(fallback = "Product"),
                    brand = "Brand",
                    barcode = null,
                    note = null,
                    image = null,
                    nutritionFacts = NutritionFacts(),
                    servingQuantity = Weight(30.grams),
                    packageQuantity = null,
                    isLiquid = false,
                )

            // 2. Create a Recipe using 2 servings of this product
            val composition =
                FoodComposition(
                    components =
                        listOf(
                            FoodCompositionComponent.Simple(
                                identity =
                                    FoodCompositionComponentIdentity.UserProduct(productId.id),
                                name = product.name,
                                image = null,
                                nutritionFacts = product.nutritionFacts,
                                quantity =
                                    FoodComponentComponentQuantity.Serving(
                                        quantity = 2.0,
                                        servingWeight = 30.grams,
                                    ),
                            )
                        )
                )
            val recipeId =
                userRecipeService.create(
                    name = FoodName(fallback = "Recipe"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    composition = composition,
                )

            // 3. Wait until recipe is created and verify initial absolute weight (2 * 30g = 60g)
            val recipe = userRecipeService.observe(recipeId).filterNotNull().first()
            assertEquals(60.grams, recipe.totalWeight)

            // 4. Update product serving weight to 40g
            val updatedProduct = product.copy(servingQuantity = Weight(40.grams))

            eventBus.publish(
                UserProductUpdatedEvent(product = updatedProduct, timestamp = Clock.System.now())
            )

            // 5. Verify the recipe has been updated and absolute weight is now 80g (2 * 40g)
            val updatedRecipe =
                userRecipeService.observe(recipeId).filterNotNull().first {
                    it.totalWeight == 80.grams
                }

            val component =
                updatedRecipe.composition.components.first() as FoodCompositionComponent.Simple
            val quantity = component.quantity as FoodComponentComponentQuantity.Serving
            assertEquals(40.grams, quantity.servingWeight)
            assertEquals(80.grams, component.quantity.absoluteWeight)
        }

    @Test
    fun updating_open_food_facts_product_updates_recipes_using_it() = runIntegrationTest {
        val userRecipeService = get<UserRecipeService>()
        val eventBus = get<EventBus>()

        val barcode = "123456789"
        val initialProductName = FoodName(fallback = "Initial OFF Product")
        val initialNutrition = NutritionFacts(proteins = NutrientValue.Complete(5.grams))

        val composition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.OpenFoodFacts(barcode),
                            name = initialProductName,
                            image = null,
                            nutritionFacts = initialNutrition,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        )
                    )
            )
        val recipeId =
            userRecipeService.create(
                name = FoodName(fallback = "Recipe"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = composition,
            )

        // Wait until recipe is created
        userRecipeService.observe(recipeId).filterNotNull().first()

        val updatedProductName = FoodName(fallback = "Updated OFF Product")
        val updatedNutrition = NutritionFacts(proteins = NutrientValue.Complete(15.grams))
        val updatedProduct =
            OpenFoodFactsProduct(
                identity = OpenFoodFactsProductIdentity(barcode),
                name = updatedProductName,
                brand = "Brand",
                nutritionFacts = updatedNutrition,
                servingQuantity = null,
                packageQuantity = null,
                thumbnail = null,
                image = null,
                source = "OFF",
            )

        eventBus.publish(
            OpenFoodFactsProductUpdatedEvent(
                product = updatedProduct,
                timestamp = Clock.System.now(),
            )
        )

        val updatedRecipe =
            userRecipeService.observe(recipeId).filterNotNull().first {
                it.composition.components.first().name == updatedProductName
            }

        val component =
            updatedRecipe.composition.components.first() as FoodCompositionComponent.Simple
        assertEquals(updatedProductName, component.name)
        assertEquals(updatedNutrition, component.nutritionFacts)
    }

    @Test
    fun updating_food_data_central_product_updates_recipes_using_it() = runIntegrationTest {
        val userRecipeService = get<UserRecipeService>()
        val eventBus = get<EventBus>()

        val fdcId = 123456
        val initialProductName = "Initial FDC Product"
        val initialNutrition = NutritionFacts(proteins = NutrientValue.Complete(8.grams))

        val composition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.FoodDataCentral(fdcId),
                            name = FoodName(fallback = initialProductName),
                            image = null,
                            nutritionFacts = initialNutrition,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        )
                    )
            )
        val recipeId =
            userRecipeService.create(
                name = FoodName(fallback = "Recipe"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = composition,
            )

        // Wait until recipe is created
        userRecipeService.observe(recipeId).filterNotNull().first()

        val updatedProductName = "Updated FDC Product"
        val updatedNutrition = NutritionFacts(proteins = NutrientValue.Complete(18.grams))
        val updatedProduct =
            FoodDataCentralProduct(
                identity = FoodDataCentralProductIdentity(fdcId),
                name = updatedProductName,
                brand = "Brand",
                barcode = null,
                source = "FDC",
                nutritionFacts = updatedNutrition,
                servingQuantity = null,
                packageQuantity = null,
            )

        eventBus.publish(
            FoodDataCentralProductUpdatedEvent(
                product = updatedProduct,
                timestamp = Clock.System.now(),
            )
        )

        // Wait until recipe is updated
        val updatedRecipe =
            userRecipeService.observe(recipeId).filterNotNull().first {
                it.composition.components.first().name.fallback == updatedProductName
            }

        val component =
            updatedRecipe.composition.components.first() as FoodCompositionComponent.Simple
        assertEquals(updatedProductName, component.name.fallback)
        assertEquals(updatedNutrition, component.nutritionFacts)
    }

    @Test
    fun updating_nested_recipe_updates_parent_recipe() = runIntegrationTest {
        val userRecipeService = get<UserRecipeService>()

        // 1. Create Child Recipe
        val childInitialName = FoodName(fallback = "Child Recipe")
        val childNutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
        val childComposition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.OpenFoodFacts("1"),
                            name = FoodName(fallback = "Ingredient"),
                            image = null,
                            nutritionFacts = childNutrition,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        )
                    )
            )
        val childId =
            userRecipeService.create(
                name = childInitialName,
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = childComposition,
            )

        // 2. Create Parent Recipe using Child Recipe
        val parentComposition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Composite(
                            identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                            name = childInitialName,
                            image = null,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                            composition = childComposition,
                        )
                    )
            )
        val parentId =
            userRecipeService.create(
                name = FoodName(fallback = "Parent Recipe"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = parentComposition,
            )

        // Wait until recipe is created
        userRecipeService.observe(parentId).filterNotNull().first()

        // 3. Update Child Recipe
        val childUpdatedName = FoodName(fallback = "Updated Child Recipe")
        userRecipeService.edit(
            identity = childId,
            name = childUpdatedName,
            note = "Updated",
            imageBytes = null,
            servings = 1.0,
            composition = childComposition,
        )

        // 4. Verify Parent Recipe is updated
        val updatedParent =
            userRecipeService.observe(parentId).filterNotNull().first {
                it.composition.components.first().name == childUpdatedName
            }

        val component =
            updatedParent.composition.components.first() as FoodCompositionComponent.Composite
        assertEquals(childUpdatedName, component.name)
    }

    @Test
    fun updating_nested_recipe_servings_updates_parent_recipe_using_it_by_serving() =
        runIntegrationTest {
            val userRecipeService = get<UserRecipeService>()

            // 1. Create Child Recipe with 1 serving = 100g
            val childComposition =
                FoodComposition(
                    components =
                        listOf(
                            FoodCompositionComponent.Simple(
                                identity = FoodCompositionComponentIdentity.OpenFoodFacts("1"),
                                name = FoodName(fallback = "Ingredient"),
                                image = null,
                                nutritionFacts = NutritionFacts(),
                                quantity = FoodComponentComponentQuantity.Weight(100.grams),
                            )
                        )
                )
            val childId =
                userRecipeService.create(
                    name = FoodName(fallback = "Child"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    composition = childComposition,
                )

            // 2. Create Parent Recipe using 2 servings of Child Recipe
            val parentComposition =
                FoodComposition(
                    components =
                        listOf(
                            FoodCompositionComponent.Composite(
                                identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                                name = FoodName(fallback = "Child"),
                                image = null,
                                quantity =
                                    FoodComponentComponentQuantity.Serving(
                                        quantity = 2.0,
                                        servingWeight = 100.grams,
                                    ),
                                composition = childComposition,
                            )
                        )
                )
            val parentId =
                userRecipeService.create(
                    name = FoodName(fallback = "Parent"),
                    note = null,
                    imageBytes = null,
                    servings = 1.0,
                    composition = parentComposition,
                )

            // Wait until recipe is created. Total weight should be 200g
            val parent = userRecipeService.observe(parentId).filterNotNull().first()
            assertEquals(200.grams, parent.totalWeight)

            // 3. Update Child Recipe to have 2 servings instead of 1.
            // New serving weight should be 100g / 2 = 50g.
            userRecipeService.edit(
                identity = childId,
                name = FoodName(fallback = "Child"),
                note = "Updated",
                imageBytes = null,
                servings = 2.0,
                composition = childComposition,
            )

            // 4. Verify Parent Recipe is updated.
            // Parent used 2 servings. Now 2 * 50g = 100g.
            val updatedParent =
                userRecipeService.observe(parentId).filterNotNull().first {
                    it.totalWeight == 100.grams
                }

            val component =
                updatedParent.composition.components.first() as FoodCompositionComponent.Composite
            val quantity = component.quantity as FoodComponentComponentQuantity.Serving
            assertEquals(50.grams, quantity.servingWeight)
        }

    @Test
    fun deleting_user_product_removes_it_from_recipes() = runIntegrationTest {
        val userRecipeService = get<UserRecipeService>()
        val userProductService = get<UserProductService>()

        val productId =
            userProductService.create(
                name = FoodName(fallback = "Product"),
                brand = "Brand",
                barcode = UserProductBarcode("123456789"),
                note = null,
                imageBytes = null,
                servingQuantity = null,
                packageQuantity = null,
                isLiquid = false,
                nutritionFacts = NutritionFacts(),
            )
        // Wait until product is created
        userProductService.observe(productId).filterNotNull().first()

        val otherIngredientId = FoodCompositionComponentIdentity.OpenFoodFacts("other")
        val composition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.UserProduct(productId.id),
                            name = FoodName(fallback = "To Delete"),
                            image = null,
                            nutritionFacts = NutritionFacts(),
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        ),
                        FoodCompositionComponent.Simple(
                            identity = otherIngredientId,
                            name = FoodName(fallback = "Keep Me"),
                            image = null,
                            nutritionFacts = NutritionFacts(),
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        ),
                    )
            )
        val recipeId =
            userRecipeService.create(
                name = FoodName(fallback = "Recipe"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = composition,
            )
        // Wait until recipe is created
        userRecipeService.observe(recipeId).filterNotNull().first()

        userProductService.delete(productId, DeleteStrategy.Delete)

        val updatedRecipe =
            userRecipeService.observe(recipeId).filterNotNull().first {
                it.composition.components.size == 1
            }

        assertEquals(1, updatedRecipe.composition.components.size)
        assertEquals(otherIngredientId, updatedRecipe.composition.components.first().identity)
    }

    @Test
    fun unlinking_user_product_anonymizes_it_in_recipes() = runIntegrationTest {
        val userProductService = get<UserProductService>()
        val userRecipeService = get<UserRecipeService>()

        val productId =
            userProductService.create(
                name = FoodName(fallback = "Product"),
                brand = "Brand",
                barcode = UserProductBarcode("123456789"),
                note = null,
                imageBytes = null,
                servingQuantity = null,
                packageQuantity = null,
                isLiquid = false,
                nutritionFacts = NutritionFacts(),
            )
        // Wait until product is created
        userProductService.observe(productId).filterNotNull().first()

        val name = FoodName(fallback = "To Unlink")
        val nutrition = NutritionFacts(proteins = NutrientValue.Complete(10.grams))
        val composition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.UserProduct(productId.id),
                            name = name,
                            image = null,
                            nutritionFacts = nutrition,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        )
                    )
            )
        val recipeId =
            userRecipeService.create(
                name = FoodName(fallback = "Recipe"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = composition,
            )

        // Wait until recipe is created
        userRecipeService.observe(recipeId).filterNotNull().first()

        userProductService.delete(productId, DeleteStrategy.Unlink)

        val updatedRecipe =
            userRecipeService.observe(recipeId).filterNotNull().first {
                it.composition.components.first().identity is
                    FoodCompositionComponentIdentity.Anonymous
            }

        assertEquals(1, updatedRecipe.composition.components.size)
        val component = updatedRecipe.composition.components.first()
        assertEquals(true, component.identity is FoodCompositionComponentIdentity.Anonymous)
        assertEquals(name, component.name)
        assertEquals(nutrition, component.nutritionFacts)
    }

    @Test
    fun deleting_nested_recipe_removes_it_from_parent_recipe() = runIntegrationTest {
        val userRecipeService = get<UserRecipeService>()

        // 1. Create Child Recipe
        val childComposition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.OpenFoodFacts("1"),
                            name = FoodName(fallback = "Ingredient"),
                            image = null,
                            nutritionFacts = NutritionFacts(),
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        )
                    )
            )
        val childId =
            userRecipeService.create(
                name = FoodName(fallback = "Child"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = childComposition,
            )

        // 2. Create Parent Recipe with 2 ingredients (one is the child recipe)
        val otherIngredientId = FoodCompositionComponentIdentity.OpenFoodFacts("other")
        val parentComposition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Composite(
                            identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                            name = FoodName(fallback = "Child"),
                            image = null,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                            composition = childComposition,
                        ),
                        FoodCompositionComponent.Simple(
                            identity = otherIngredientId,
                            name = FoodName(fallback = "Other"),
                            image = null,
                            nutritionFacts = NutritionFacts(),
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        ),
                    )
            )
        val parentId =
            userRecipeService.create(
                name = FoodName(fallback = "Parent"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = parentComposition,
            )

        // Wait until recipe is created
        userRecipeService.observe(parentId).filterNotNull().first()

        // 3. Delete Child Recipe
        userRecipeService.delete(childId, DeleteStrategy.Delete)

        // 4. Verify Parent Recipe has one less ingredient
        val updatedParent =
            userRecipeService.observe(parentId).filterNotNull().first {
                it.composition.components.size == 1
            }

        assertEquals(1, updatedParent.composition.components.size)
        assertEquals(otherIngredientId, updatedParent.composition.components.first().identity)
    }

    @Test
    fun unlinking_nested_recipe_anonymizes_it_in_parent_recipe() = runIntegrationTest {
        val userRecipeService = get<UserRecipeService>()

        // 1. Create Child Recipe
        val childComposition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Simple(
                            identity = FoodCompositionComponentIdentity.OpenFoodFacts("1"),
                            name = FoodName(fallback = "Ingredient"),
                            image = null,
                            nutritionFacts = NutritionFacts(),
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                        )
                    )
            )
        val childId =
            userRecipeService.create(
                name = FoodName(fallback = "Child"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = childComposition,
            )

        // 2. Create Parent Recipe
        val parentComposition =
            FoodComposition(
                components =
                    listOf(
                        FoodCompositionComponent.Composite(
                            identity = FoodCompositionComponentIdentity.Recipe(childId.id),
                            name = FoodName(fallback = "Child"),
                            image = null,
                            quantity = FoodComponentComponentQuantity.Weight(100.grams),
                            composition = childComposition,
                        )
                    )
            )
        val parentId =
            userRecipeService.create(
                name = FoodName(fallback = "Parent"),
                note = null,
                imageBytes = null,
                servings = 1.0,
                composition = parentComposition,
            )

        // Wait until recipe is created
        userRecipeService.observe(parentId).filterNotNull().first()

        // 3. Unlink Child Recipe
        userRecipeService.delete(childId, DeleteStrategy.Unlink)

        // 4. Verify Parent Recipe has anonymous ingredient
        val updatedParent =
            userRecipeService.observe(parentId).filterNotNull().first {
                it.composition.components.first().identity is
                    FoodCompositionComponentIdentity.Anonymous
            }

        assertEquals(1, updatedParent.composition.components.size)
        assertEquals(
            true,
            updatedParent.composition.components.first().identity
                is FoodCompositionComponentIdentity.Anonymous,
        )
    }

    private fun testModule(testScope: TestScope) = module {
        applicationCoroutineScope { testScope.backgroundScope }
        single<EventBus> { LoggingEventBus(InMemoryEventBus(), Logger) }
        single<BlobStorage> { FileKitBlobStorage() }

        single { eventStoreDatabase.eventStoreDao }
        factoryOf(::RoomEventStore).bind<EventStore>()

        single { readModelDatabase.compositionDao }
        factoryOf(::RoomUserRecipeCompositionRepository).bind<UserRecipeCompositionRepository>()

        factoryOf(::UserRecipeService)
        factoryOf(::UserProductService)

        eventHandlerOf(::UserProductSynchronizer)
        eventHandlerOf(::OpenFoodFactsSynchronizer)
        eventHandlerOf(::FoodDataCentralSynchronizer)
        eventHandlerOf(::NestedRecipeSynchronizer)
        eventHandlerOf(::CompositionSynchronizer)
    }

    private fun runIntegrationTest(block: suspend Koin.() -> Unit) =
        runTest(timeout = 5.seconds) {
            val testScope = this
            val koin = koinApplication { modules(testModule(testScope)) }
            try {
                block(koin.koin)
            } finally {
                koin.close()
            }
        }
}
