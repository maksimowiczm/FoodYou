package com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.domain.Measurement
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.MeasurementType
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.RecipeIngredientEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class RoomRecipeDataSource(
    private val recipeDao: RecipeDao,
    private val productDataSource: LocalProductDataSource,
) : LocalRecipeDataSource {
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
                                measurement = measurementFrom(it.measurement, it.quantity),
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

    override suspend fun insertRecipe(recipe: Recipe): FoodId.Recipe {
        val recipeEntity = recipe.toEntity()

        val ingredients =
            recipe.ingredients.map { (food, measurement) ->
                val foodId = food.id

                RecipeIngredientEntity(
                    ingredientRecipeId = (foodId as? FoodId.Recipe)?.id,
                    ingredientProductId = (foodId as? FoodId.Product)?.id,
                    measurement = measurement.toEntityType(),
                    quantity = measurement.toEntityValue(),
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
                    measurement = measurement.toEntityType(),
                    quantity = measurement.toEntityValue(),
                )
            }

        recipeDao.updateRecipeWithIngredients(recipeEntity, ingredients)
    }
}

private fun Recipe.toEntity(): RecipeEntity =
    RecipeEntity(
        name = this.name,
        servings = this.servings,
        note = this.note,
        isLiquid = this.isLiquid,
    )

private fun Measurement.toEntityType(): MeasurementType =
    when (this) {
        is Measurement.Gram -> MeasurementType.Gram
        is Measurement.Milliliter -> MeasurementType.Milliliter
        is Measurement.Package -> MeasurementType.Package
        is Measurement.Serving -> MeasurementType.Serving
    }

private fun Measurement.toEntityValue(): Double =
    when (this) {
        is Measurement.Gram -> this.value
        is Measurement.Milliliter -> this.value
        is Measurement.Package -> this.quantity
        is Measurement.Serving -> this.quantity
    }

private val RecipeIngredientEntity.foodId: FoodId
    get() =
        ingredientRecipeId?.let { FoodId.Recipe(it) }
            ?: ingredientProductId?.let { FoodId.Product(it) }
            ?: error(
                "RecipeIngredientEntity must have either ingredientRecipeId or ingredientProductId set"
            )

private fun measurementFrom(type: MeasurementType, rawValue: Double): Measurement =
    when (type) {
        MeasurementType.Gram -> Measurement.Gram(rawValue)
        MeasurementType.Milliliter -> Measurement.Milliliter(rawValue)
        MeasurementType.Package -> Measurement.Package(rawValue)
        MeasurementType.Serving -> Measurement.Serving(rawValue)
    }
