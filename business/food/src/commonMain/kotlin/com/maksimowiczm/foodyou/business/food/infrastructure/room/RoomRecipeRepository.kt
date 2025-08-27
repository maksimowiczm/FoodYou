package com.maksimowiczm.foodyou.business.food.infrastructure.room

import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.food.domain.RecipeRepository
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeIngredientEntity
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.from
import com.maksimowiczm.foodyou.shared.common.domain.measurement.rawValue
import com.maksimowiczm.foodyou.shared.common.domain.measurement.type
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class RoomRecipeRepository(
    private val recipeDao: RecipeDao,
    private val productDataSource: RoomProductRepository,
) : RecipeRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeRecipe(recipeId: FoodId.Recipe): Flow<Recipe?> =
        combine(
                recipeDao.observeRecipe(recipeId.id),
                recipeDao.observeRecipeIngredients(recipeId.id),
            ) { recipeEntity, ingredients ->
                if (recipeEntity == null) {
                    return@combine null
                }

                val ingredients =
                    ingredients.map {
                        val foodId = it.foodId

                        val foodFlow =
                            when (foodId) {
                                is FoodId.Recipe -> observeRecipe(foodId).filterNotNull()
                                is FoodId.Product ->
                                    productDataSource.observeProduct(foodId).filterNotNull()
                            }

                        foodFlow.map { food ->
                            RecipeIngredient(
                                food = food,
                                measurement = Measurement.from(it.measurement, it.quantity),
                            )
                        }
                    }

                recipeEntity to ingredients.combine()
            }
            .flatMapLatest { pair ->
                if (pair == null) {
                    return@flatMapLatest flowOf(null)
                } else {
                    val (recipeEntity, ingredients) = pair

                    ingredients.map { ingredients ->
                        Recipe(
                            id = FoodId.Recipe(recipeEntity.id),
                            name = recipeEntity.name,
                            servings = recipeEntity.servings,
                            note = recipeEntity.note,
                            isLiquid = recipeEntity.isLiquid,
                            ingredients = ingredients,
                        )
                    }
                }
            }

    override suspend fun deleteRecipe(recipe: Recipe) {
        val entity = recipe.toEntity()
        recipeDao.delete(entity)
    }

    override suspend fun insertRecipe(
        name: String,
        servings: Int,
        note: String?,
        isLiquid: Boolean,
        ingredients: List<RecipeIngredient>,
    ): FoodId.Recipe {
        val recipe =
            Recipe(
                id = FoodId.Recipe(0L),
                name = name,
                servings = servings,
                note = note,
                isLiquid = isLiquid,
                ingredients = ingredients,
            )

        val recipeEntity = recipe.toEntity()

        val ingredients =
            recipe.ingredients.map { (food, measurement) ->
                val foodId = food.id

                RecipeIngredientEntity(
                    ingredientRecipeId = (foodId as? FoodId.Recipe)?.id,
                    ingredientProductId = (foodId as? FoodId.Product)?.id,
                    measurement = measurement.type,
                    quantity = measurement.rawValue,
                )
            }

        val id =
            recipeDao
                .insertRecipeWithIngredients(recipe = recipeEntity, ingredients = ingredients)
                .let(FoodId::Recipe)

        return id
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        val recipeEntity = recipe.toEntity()

        val ingredients =
            recipe.ingredients.map { (food, measurement) ->
                val foodId = food.id

                RecipeIngredientEntity(
                    ingredientRecipeId = (foodId as? FoodId.Recipe)?.id,
                    ingredientProductId = (foodId as? FoodId.Product)?.id,
                    measurement = measurement.type,
                    quantity = measurement.rawValue,
                )
            }

        recipeDao.updateRecipeWithIngredients(recipeEntity, ingredients)
    }
}

private fun Recipe.toEntity(): RecipeEntity =
    RecipeEntity(
        id = this.id.id,
        name = this.name,
        servings = this.servings,
        note = this.note,
        isLiquid = this.isLiquid,
    )

private val RecipeIngredientEntity.foodId: FoodId
    get() =
        ingredientRecipeId?.let { FoodId.Recipe(it) }
            ?: ingredientProductId?.let { FoodId.Product(it) }
            ?: error(
                "RecipeIngredientEntity must have either ingredientRecipeId or ingredientProductId set"
            )
