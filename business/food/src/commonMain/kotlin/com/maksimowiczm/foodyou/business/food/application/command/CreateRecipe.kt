package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlinx.coroutines.flow.firstOrNull

data class CreateRecipeCommand(
    val name: String,
    val servings: Int,
    val note: String?,
    val isLiquid: Boolean,
    val ingredients: List<Pair<FoodId, Measurement>>,
    val event: FoodEvent.FoodCreationEvent,
) : Command<FoodId.Recipe, CreateRecipeError> {}

sealed interface CreateRecipeError {
    data object EmptyName : CreateRecipeError

    data object NonPositiveServings : CreateRecipeError

    data object EmptyIngredients : CreateRecipeError

    data class IngredientNotFound(val foodId: FoodId) : CreateRecipeError

    data object CircularIngredient : CreateRecipeError
}

internal class CreateRecipeCommandHandler(
    private val recipeDataSource: LocalRecipeDataSource,
    private val productDataSource: LocalProductDataSource,
    private val eventDataSource: LocalFoodEventDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<CreateRecipeCommand, FoodId.Recipe, CreateRecipeError> {

    override suspend fun handle(
        command: CreateRecipeCommand
    ): Result<FoodId.Recipe, CreateRecipeError> {
        if (command.name.isBlank()) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateRecipeError.EmptyName,
                message = { "Recipe name cannot be empty." },
            )
        }

        if (command.servings <= 0) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateRecipeError.NonPositiveServings,
                message = { "Recipe servings must be a positive integer." },
            )
        }

        if (command.ingredients.isEmpty()) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateRecipeError.EmptyIngredients,
                message = { "Recipe must have at least one ingredient." },
            )
        }

        val ingredients =
            command.ingredients.map { (foodId, measurement) ->
                val food =
                    when (foodId) {
                        is FoodId.Product -> productDataSource.observeProduct(foodId).firstOrNull()
                        is FoodId.Recipe -> recipeDataSource.observeRecipe(foodId).firstOrNull()
                    }

                if (food == null) {
                    return ErrorLoggingUtils.logAndReturnFailure(
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
                name = command.name,
                servings = command.servings,
                note = command.note?.takeIf { it.isNotBlank() },
                isLiquid = command.isLiquid,
                ingredients = ingredients,
            )

        // Check for circular references in ingredients
        val flatIngredients = recipe.flatIngredients()
        val ingredientIds = flatIngredients.map { it.id }.toSet()
        if (flatIngredients.size != ingredientIds.size) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateRecipeError.CircularIngredient,
                message = { "Recipe contains circular ingredient references." },
            )
        }

        val recipeId =
            transactionProvider.withTransaction {
                val recipeId = recipeDataSource.insertRecipe(recipe)
                eventDataSource.insert(recipeId, command.event)
                recipeId
            }

        return Ok(recipeId)
    }

    private companion object {
        private const val TAG = "CreateRecipeCommandHandler"
    }
}
