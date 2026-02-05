package com.maksimowiczm.foodyou.recipe.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.IntegrationEvent
import com.maksimowiczm.foodyou.common.infrastructure.filekit.directory
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.recipe.domain.FoodReference
import com.maksimowiczm.foodyou.recipe.domain.Recipe
import com.maksimowiczm.foodyou.recipe.domain.RecipeDeletedEvent
import com.maksimowiczm.foodyou.recipe.domain.RecipeIdentity
import com.maksimowiczm.foodyou.recipe.domain.RecipeIngredient
import com.maksimowiczm.foodyou.recipe.domain.RecipeName
import com.maksimowiczm.foodyou.recipe.domain.RecipeRepository
import com.maksimowiczm.foodyou.recipe.domain.RecipeSearchParameters
import com.maksimowiczm.foodyou.recipe.infrastructure.room.FoodReferenceType
import com.maksimowiczm.foodyou.recipe.infrastructure.room.RecipeDao
import com.maksimowiczm.foodyou.userfood.domain.FoodNote
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class RecipeRepositoryImpl(
    private val dao: RecipeDao,
    private val integrationEventBus: EventBus<IntegrationEvent>,
) : RecipeRepository {
    private val mapper = RecipeMapper()

    @OptIn(ExperimentalPagingApi::class)
    override fun search(
        parameters: RecipeSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<Recipe>> {
        when (parameters.query) {
            SearchQuery.Blank,
            is SearchQuery.Text -> Unit

            is SearchQuery.Barcode,
            is SearchQuery.FoodDataCentralUrl,
            is SearchQuery.OpenFoodFactsUrl -> {
                return flowOf(
                    PagingData.empty(
                        sourceLoadStates =
                            LoadStates(NotLoading(true), NotLoading(true), NotLoading(true))
                    )
                )
            }
        }

        val config = PagingConfig(pageSize = pageSize)
        val factory = {
            when (parameters.query) {
                SearchQuery.Blank -> dao.getPagingSource(parameters.accountId.value)

                is SearchQuery.Text ->
                    dao.getPagingSourceByQuery(parameters.query.query, parameters.accountId.value)

                is SearchQuery.Barcode,
                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> error("Unreachable")
            }
        }

        return Pager(config = config, pagingSourceFactory = factory).flow.map { data ->
            data.map(mapper::toDomain)
        }
    }

    override fun count(parameters: RecipeSearchParameters): Flow<Int> {
        val countFlow: Flow<Int> =
            when (parameters.query) {
                SearchQuery.Blank -> dao.observeCount(parameters.accountId.value)

                is SearchQuery.Text ->
                    dao.observeCountByQuery(parameters.query.query, parameters.accountId.value)

                is SearchQuery.Barcode,
                is SearchQuery.OpenFoodFactsUrl,
                is SearchQuery.FoodDataCentralUrl -> flowOf(0)
            }

        return countFlow
    }

    override suspend fun create(
        accountId: LocalAccountId,
        name: RecipeName,
        servings: Double,
        image: Image.Local?,
        note: FoodNote?,
        finalWeight: Double?,
        ingredients: List<RecipeIngredient>,
    ): RecipeIdentity {
        require(ingredients.isNotEmpty()) { "Recipe must have at least one ingredient" }
        require(servings > 0) { "Recipe must have a positive number of servings" }
        require(finalWeight == null || finalWeight > 0) { "Final weight must be a positive number" }

        val uuid = Uuid.random().toString()

        val recipeDirectory = accountId.directory() / "recipes"
        recipeDirectory.createDirectories()

        val photoPath =
            if (image != null) {
                val sourceFile = PlatformFile(image.uri)
                require(sourceFile.exists()) { "Image file does not exist at path: ${image.uri}" }
                val bytes = sourceFile.readBytes()

                val compressed =
                    FileKit.compressImage(
                        bytes = bytes,
                        quality = 85,
                        imageFormat = ImageFormat.JPEG,
                    )

                val dest = (recipeDirectory / "$uuid.jpg").apply { write(compressed) }
                dest.path
            } else {
                null
            }

        val recipe =
            Recipe(
                identity = RecipeIdentity(uuid, accountId),
                name = name,
                servings = servings,
                image = photoPath?.let { Image.Local(it) },
                note = note,
                finalWeight = finalWeight,
                ingredients = ingredients,
            )

        val recipeEntity = mapper.toEntity(recipe)
        val ingredientEntities = mapper.toIngredientEntities(ingredients)
        dao.insertRecipeWithIngredients(recipeEntity, ingredientEntities)

        return RecipeIdentity(uuid, accountId)
    }

    override suspend fun update(
        identity: RecipeIdentity,
        name: RecipeName,
        servings: Double,
        image: Image.Local?,
        note: FoodNote?,
        finalWeight: Double?,
        ingredients: List<RecipeIngredient>,
    ) {
        require(ingredients.isNotEmpty()) { "Recipe must have at least one ingredient" }
        require(servings > 0) { "Recipe must have a positive number of servings" }
        require(finalWeight == null || finalWeight > 0) { "Final weight must be a positive number" }

        val existingEntity = dao.observe(identity.id, identity.accountId.value).first()

        requireNotNull(existingEntity) { "Cannot edit non-existing recipe with id: ${identity.id}" }

        val uuid = identity.id
        val recipeDirectory = identity.accountId.directory() / "recipes"
        recipeDirectory.createDirectories()

        val imagePath: String? =
            if (image != null && existingEntity.recipe.imagePath != image.uri) {
                val sourceFile = PlatformFile(image.uri)
                require(sourceFile.exists()) { "Image file does not exist at path: ${image.uri}" }
                val bytes = sourceFile.readBytes()

                val compressed =
                    FileKit.compressImage(
                        bytes = bytes,
                        quality = 85,
                        imageFormat = ImageFormat.JPEG,
                    )

                val dest = (recipeDirectory / "$uuid.jpg").apply { write(compressed) }

                dest.path
            } else if (image == null && existingEntity.recipe.imagePath != null) {
                val existingPath = existingEntity.recipe.imagePath
                val existingFile = PlatformFile(existingPath)
                if (existingFile.exists()) {
                    existingFile.delete()
                }
                null
            } else {
                existingEntity.recipe.imagePath
            }

        val recipe =
            Recipe(
                identity = identity,
                name = name,
                servings = servings,
                image = imagePath?.let { Image.Local(it) },
                note = note,
                finalWeight = finalWeight,
                ingredients = ingredients,
            )

        val updatedEntity = mapper.toEntity(recipe, sqliteId = existingEntity.recipe.sqliteId)
        val ingredientEntities = mapper.toIngredientEntities(ingredients)
        dao.updateRecipeWithIngredients(updatedEntity, ingredientEntities)
    }

    override fun observe(identity: RecipeIdentity): Flow<Recipe?> =
        dao.observe(identity.id, identity.accountId.value).map { entity ->
            entity?.let(mapper::toDomain)
        }

    override suspend fun delete(identity: RecipeIdentity) {
        val existingEntity = dao.observe(identity.id, identity.accountId.value).first()

        requireNotNull(existingEntity) {
            "Cannot delete non-existing recipe with id: ${identity.id}"
        }

        dao.deleteRecipe(existingEntity.recipe)
        PlatformFile("${existingEntity.recipe.uuid}.jpg").delete(mustExist = false)

        integrationEventBus.publish(RecipeDeletedEvent(identity))
    }

    override suspend fun findRecipesUsingFood(
        foodReference: FoodReference,
        accountId: LocalAccountId,
    ): List<Recipe> {
        val foodReferenceType =
            when (foodReference) {
                is FoodReference.UserFood -> FoodReferenceType.UserFood
                is FoodReference.FoodDataCentral -> FoodReferenceType.FoodDataCentral
                is FoodReference.OpenFoodFacts -> FoodReferenceType.OpenFoodFacts
                is FoodReference.Recipe -> FoodReferenceType.Recipe
            }

        return dao.findRecipesUsingFood(
                accountId = accountId.value,
                foodReferenceType = foodReferenceType,
                foodId = foodReference.foodId,
            )
            .map { mapper.toDomain(it) }
    }
}
