package com.maksimowiczm.foodyou.feature.food.ui.recipe.update

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.Recipe
import com.maksimowiczm.foodyou.feature.food.data.database.food.RecipeIngredient
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeFormState
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeViewModel
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateRecipeViewModel(
    observeRecipeUseCase: ObserveRecipeUseCase,
    foodDatabase: FoodDatabase,
    private val foodId: FoodId.Recipe,
    productMapper: ProductMapper
) : RecipeViewModel(
    productDao = foodDatabase.productDao,
    productMapper = productMapper,
    observeRecipeUseCase = observeRecipeUseCase
) {

    private val recipeDao = foodDatabase.recipeDao

    val recipe = observeRecipeUseCase(foodId).stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(2_000)
    )

    private val eventBus = Channel<UpdateRecipeEvent>()
    val events = eventBus.receiveAsFlow()

    fun update(form: RecipeFormState) {
        if (!form.isValid) {
            Logger.w(TAG) { "Form is not valid, cannot update recipe." }
            return
        }

        if (!form.isModified) {
            Logger.d(TAG) { "Form is not modified, no need to update recipe." }
            viewModelScope.launch {
                eventBus.send(UpdateRecipeEvent.Updated)
            }
            return
        }

        val ingredients = form.ingredients.map { ingredient ->
            RecipeIngredient(
                ingredientRecipeId = (ingredient.foodId as? FoodId.Recipe)?.id,
                ingredientProductId = (ingredient.foodId as? FoodId.Product)?.id,
                measurement = ingredient.measurement.type,
                quantity = ingredient.measurement.rawValue
            )
        }

        // Check circular reference
        if (ingredients.any { it.ingredientRecipeId == foodId.id }) {
            Logger.w(TAG) { "Circular reference detected in ingredients, cannot update recipe." }
            return
        }

        val recipe = Recipe(
            id = foodId.id,
            name = form.name.value,
            servings = form.servings.value,
            note = form.note.value,
            isLiquid = form.isLiquid
        )

        viewModelScope.launch {
            recipeDao.updateRecipeWithIngredients(
                recipe = recipe,
                ingredients = ingredients
            )

            eventBus.send(UpdateRecipeEvent.Updated)
        }
    }

    private companion object {
        const val TAG = "UpdateRecipeViewModel"
    }
}
