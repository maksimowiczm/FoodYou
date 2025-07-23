package com.maksimowiczm.foodyou.feature.food.ui.recipe

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.food.data.database.food.ProductDao
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.Recipe
import com.maksimowiczm.foodyou.feature.food.domain.RecipeIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapIfNotNull

internal abstract class RecipeViewModel(
    private val productDao: ProductDao,
    private val productMapper: ProductMapper,
    private val observeRecipeUseCase: ObserveRecipeUseCase
) : ViewModel() {

    fun intoRecipe(state: RecipeFormState): Flow<Recipe?> {
        if (state.ingredients.isEmpty()) {
            return flowOf(null)
        }

        return state.ingredients.map { ingredient ->
            when (ingredient.foodId) {
                is FoodId.Product ->
                    productDao
                        .observe(ingredient.foodId.id)
                        .mapIfNotNull(productMapper::toModel)

                is FoodId.Recipe -> observeRecipeUseCase(ingredient.foodId)
            }.filterNotNull().map { food ->
                RecipeIngredient(
                    food = food,
                    measurement = ingredient.measurement
                )
            }
        }.combine().map { ingredients ->
            Recipe(
                id = FoodId.Recipe(-1),
                name = state.name.value,
                servings = state.servings.value,
                note = state.note.value,
                ingredients = ingredients,
                isLiquid = state.isLiquid
            )
        }
    }
}
