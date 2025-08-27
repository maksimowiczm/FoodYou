package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.business.food.application.ObserveFoodUseCase
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.RecipeIngredient
import com.maksimowiczm.foodyou.feature.food.recipe.ui.RecipeFormState
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal abstract class RecipeViewModel(private val observeFoodUseCase: ObserveFoodUseCase) :
    ViewModel() {

    fun intoRecipe(state: RecipeFormState): Flow<Recipe?> {
        if (state.ingredients.isEmpty()) {
            return flowOf(null)
        }

        return state.ingredients
            .map { ingredient ->
                observeFoodUseCase.observe(ingredient.foodId).filterNotNull().map { food ->
                    RecipeIngredient(food = food, measurement = ingredient.measurement)
                }
            }
            .combine()
            .map { ingredients ->
                Recipe(
                    id = FoodId.Recipe(-1),
                    name = state.name.value,
                    servings = state.servings.value,
                    note = state.note.value,
                    ingredients = ingredients,
                    isLiquid = state.isLiquid,
                )
            }
    }
}
