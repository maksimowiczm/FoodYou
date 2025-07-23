package com.maksimowiczm.foodyou.feature.food.ui.recipe.create

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.Recipe as RecipeEntity
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeIngredient as RecipeIngredientEntity
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeFormState
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeViewModel
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class CreateRecipeViewModel(
    foodDatabase: FoodDatabase,
    observeFoodUseCase: ObserveFoodUseCase
) : RecipeViewModel(observeFoodUseCase) {

    private val recipeDao = foodDatabase.recipeDao

    private val eventBus = Channel<CreateRecipeEvent>()
    val events = eventBus.receiveAsFlow()

    fun create(form: RecipeFormState) {
        if (!form.isValid) {
            Logger.w(TAG) { "Attempted to create recipe with invalid form state: $form" }
        }

        val ingredients = form.ingredients.map { ingredient ->
            RecipeIngredientEntity(
                ingredientRecipeId = (ingredient.foodId as? FoodId.Recipe)?.id,
                ingredientProductId = (ingredient.foodId as? FoodId.Product)?.id,
                measurement = ingredient.measurement.type,
                quantity = ingredient.measurement.rawValue
            )
        }

        val recipe = RecipeEntity(
            name = form.name.value,
            servings = form.servings.value,
            note = form.note.value,
            isLiquid = form.isLiquid
        )

        viewModelScope.launch {
            val id = recipeDao.insertRecipeWithIngredients(
                recipe = recipe,
                ingredients = ingredients
            )

            eventBus.send(CreateRecipeEvent.Created(FoodId.Recipe(id)))
        }
    }

    private companion object {
        private const val TAG = "CreateRecipeViewModel"
    }
}
