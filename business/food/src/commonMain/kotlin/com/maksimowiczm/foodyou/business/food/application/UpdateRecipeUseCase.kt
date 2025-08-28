package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.food.domain.RecipeRepository
import com.maksimowiczm.foodyou.business.shared.application.database.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.firstOrNull

sealed interface UpdateRecipeError {
    data object EmptyName : UpdateRecipeError

    data object NonPositiveServings : UpdateRecipeError

    data object EmptyIngredients : UpdateRecipeError

    data class RecipeNotFound(val id: FoodId.Recipe) : UpdateRecipeError

    data class IngredientNotFound(val foodId: FoodId) : UpdateRecipeError

    data object CircularIngredient : UpdateRecipeError
}

fun interface UpdateRecipeUseCase {
    suspend fun update(
        id: FoodId.Recipe,
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>,
    ): Result<Unit, UpdateRecipeError>
}

internal class UpdateRecipeUseCaseImpl(
    private val recipeRepository: RecipeRepository,
    private val productRepository: ProductRepository,
    private val eventRepository: FoodEventRepository,
    private val transactionProvider: DatabaseTransactionProvider,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : UpdateRecipeUseCase {
    override suspend fun update(
        id: FoodId.Recipe,
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<Pair<FoodId, Measurement>>,
    ): Result<Unit, UpdateRecipeError> {
        if (name.isBlank()) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.EmptyName,
                message = { "Recipe name cannot be empty." },
            )
        }

        if (servings <= 0) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.NonPositiveServings,
                message = { "Recipe servings must be a positive integer." },
            )
        }

        if (ingredients.isEmpty()) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.EmptyIngredients,
                message = { "Recipe must have at least one ingredient." },
            )
        }

        val recipe = recipeRepository.observeRecipe(id).firstOrNull()

        if (recipe == null) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateRecipeError.RecipeNotFound(id),
                message = { "Recipe with ID $id not found." },
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
                            error = UpdateRecipeError.IngredientNotFound(foodId),
                            message = { "Ingredient with ID $foodId not found." },
                        )
                    }

                    RecipeIngredient(food, measurement)
                }

            val updatedRecipe =
                recipe.copy(
                    name = name,
                    servings = servings,
                    note = note,
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
                    error = UpdateRecipeError.CircularIngredient,
                    message = { "Recipe contains circular ingredient references." },
                )
            }

            recipeRepository.updateRecipe(updatedRecipe)
            eventRepository.insert(id, FoodEvent.Edited(dateProvider.now()))
            Ok(Unit)
        }
    }

    private companion object {
        const val TAG = "UpdateRecipeUseCaseImpl"
    }
}
