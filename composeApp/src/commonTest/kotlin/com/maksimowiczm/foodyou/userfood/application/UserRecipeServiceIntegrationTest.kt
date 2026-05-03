package com.maksimowiczm.foodyou.userfood.application

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.BlobDigest
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.event.ListEventBus
import com.maksimowiczm.foodyou.common.infrastructure.filekit.FileKitBlobStorage
import com.maksimowiczm.foodyou.common.infrastructure.provideRoomDatabaseBuilder
import com.maksimowiczm.foodyou.userfood.domain.recipe.FoodReference
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeDeletedEvent
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIdentity
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIngredient
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeName
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeRepository
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.UserFoodDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.UserRecipeRepositoryImpl
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import okio.ByteString.Companion.toByteString

class UserRecipeServiceIntegrationTest {
    private lateinit var database: UserFoodDatabase
    private lateinit var repository: UserRecipeRepository
    private lateinit var service: UserRecipeService
    private lateinit var eventBus: ListEventBus
    private lateinit var blobStorage: BlobStorage

    @BeforeTest
    fun setup() {
        database = provideRoomDatabaseBuilder<UserFoodDatabase>().buildDatabase()
        eventBus = ListEventBus()
        blobStorage = FileKitBlobStorage()
        repository = UserRecipeRepositoryImpl(database = database)
        service =
            UserRecipeService(
                repository = repository,
                blobStorage = blobStorage,
                eventBus = eventBus,
            )
    }

    @AfterTest
    fun tearDown() {
        if (this::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun delete_publishesRecipeDeletedEvent() = runTest {
        // Arrange - Create recipe
        val createResult =
            service.create(
                name = UserRecipeName("Recipe to Delete"),
                servings = 1.0,
                imageBytes = null,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        // Act - Delete recipe
        service.delete(identity)

        // Assert - Event was published with correct data
        val publishedEvents = eventBus.publishedEvents
        assertEquals(1, publishedEvents.size)
        val event = assertIs<UserRecipeDeletedEvent>(publishedEvents[0])
        assertEquals(identity, event.identity)
        assertEquals(identity.id, event.identity.id)
    }

    @Test
    fun create_withImage_storesBlobAndPersistsDigest() = runTest {
        val sourceBytes = "recipe-image-create-${Uuid.random()}".encodeToByteArray()

        val createResult =
            service.create(
                name = UserRecipeName("Recipe with image"),
                servings = 1.0,
                imageBytes = sourceBytes,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )

        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data
        val storedRecipe = repository.observe(identity).first()
        assertNotNull(storedRecipe)

        val expectedDigest = BlobDigest("sha256:" + sourceBytes.toByteString().sha256().hex())
        assertEquals(expectedDigest, storedRecipe.image)

        val expectedUri = blobStorage.resolve(expectedDigest)
        val storedBytes = PlatformFile(expectedUri.value).readBytes()
        assertContentEquals(sourceBytes, storedBytes)
    }

    @Test
    fun update_withNewImage_storesNewBlobAndUpdatesRecipeImage() = runTest {
        val initialBytes = "recipe-image-initial-${Uuid.random()}".encodeToByteArray()

        val createResult =
            service.create(
                name = UserRecipeName("Recipe image update"),
                servings = 1.0,
                imageBytes = initialBytes,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        val updatedBytes = "recipe-image-updated-${Uuid.random()}".encodeToByteArray()
        val updateResult =
            service.update(
                identity = identity,
                name = UserRecipeName("Recipe image update"),
                servings = 1.0,
                imageBytes = updatedBytes,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val _ = assertIs<Result.Success<Unit, *>>(updateResult)

        val storedRecipe = repository.observe(identity).first()
        assertNotNull(storedRecipe)

        val expectedDigest = BlobDigest("sha256:" + updatedBytes.toByteString().sha256().hex())
        assertEquals(expectedDigest, storedRecipe.image)

        val updatedUri = blobStorage.resolve(expectedDigest)
        assertContentEquals(updatedBytes, PlatformFile(updatedUri.value).readBytes())
    }

    @Test
    fun delete_withImage_keepsStoredBlobFileAvailable() = runTest {
        val sourceBytes = "recipe-image-delete-${Uuid.random()}".encodeToByteArray()

        val createResult =
            service.create(
                name = UserRecipeName("Recipe image delete"),
                servings = 1.0,
                imageBytes = sourceBytes,
                note = null,
                ingredients =
                    listOf(
                        UserRecipeIngredient(
                            foodReference = FoodReference.UserProduct(Uuid.random()),
                            quantity = AbsoluteQuantity.Weight(100.grams),
                        )
                    ),
            )
        val identity = assertIs<Result.Success<UserRecipeIdentity, *>>(createResult).data

        service.delete(identity)

        assertNull(repository.observe(identity).firstOrNull())

        val storedUri =
            blobStorage.resolve(BlobDigest("sha256:" + sourceBytes.toByteString().sha256().hex()))
        val storedBytes = PlatformFile(storedUri.value).readBytes()
        assertContentEquals(sourceBytes, storedBytes)
    }
}
