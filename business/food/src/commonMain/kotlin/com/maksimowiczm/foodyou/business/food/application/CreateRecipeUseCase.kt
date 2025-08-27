package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.food.domain.RecipeRepository
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.firstOrNull

sealed interface CreateRecipeError {
    data object EmptyName : CreateRecipeError

    data object NonPositiveServings : CreateRecipeError

    data object EmptyIngredients : CreateRecipeError

    data class IngredientNotFound(val foodId: FoodId) : CreateRecipeError

    data object CircularIngredient : CreateRecipeError
}

fun interface CreateRecipeUseCase {
    suspend fun create(
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>,
        event: FoodEvent.FoodCreationEvent,
    ): Result<FoodId.Recipe, CreateRecipeError>
}

internal class CreateRecipeUseCaseImpl(
    private val recipeRepository: RecipeRepository,
    private val productRepository: ProductRepository,
    private val eventRepository: FoodEventRepository,
    private val transactionProvider: DatabaseTransactionProvider,
    private val logger: Logger,
) : CreateRecipeUseCase {
    override suspend fun create(
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>,
        event: FoodEvent.FoodCreationEvent,
    ): Result<FoodId.Recipe, CreateRecipeError> {
        if (name.isBlank()) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateRecipeError.EmptyName,
                message = { "Recipe name cannot be empty." },
            )
        }

        if (servings <= 0) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateRecipeError.NonPositiveServings,
                message = { "Recipe servings must be a positive integer." },
            )
        }

        if (ingredients.isEmpty()) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateRecipeError.EmptyIngredients,
                message = { "Recipe must have at least one ingredient." },
            )
        }

        return transactionProvider.withTransaction {
            val ingredients =
                ingredients.map { (foodId, measurement) ->
                    val food =
                        when (foodId) {
                            is FoodId.Product ->
                                productRepository.observeProduct(foodId).firstOrNull()

                            is FoodId.Recipe -> recipeRepository.observeRecipe(foodId).firstOrNull()
                        }

                    if (food == null) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = CreateRecipeError.IngredientNotFound(foodId),
                            message = { "Ingredient with ID $foodId not found." },
                        )
                    }

                    RecipeIngredient(food, measurement)
                }

            val recipe =
                Recipe(
                    id = FoodId.Recipe(0L),
                    name = name,
                    servings = servings,
                    note = note?.takeIf { it.isNotBlank() },
                    isLiquid = isLiquid,
                    ingredients = ingredients,
                )

            // Check for circular references in ingredients
            val flatIngredients = recipe.flatIngredients()
            val ingredientIds = flatIngredients.map { it.id }.toSet()
            if (flatIngredients.size != ingredientIds.size) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = CreateRecipeError.CircularIngredient,
                    message = { "Recipe contains circular ingredient references." },
                )
            }

            val recipeId =
                recipeRepository.insertRecipe(
                    name = name,
                    servings = servings,
                    note = note?.takeIf { it.isNotBlank() },
                    isLiquid = isLiquid,
                    ingredients = ingredients,
                )
            eventRepository.insert(recipeId, event)
            Ok(recipeId)
        }
    }

    private companion object {
        const val TAG = "CreateRecipeUseCaseImpl"
    }
}
