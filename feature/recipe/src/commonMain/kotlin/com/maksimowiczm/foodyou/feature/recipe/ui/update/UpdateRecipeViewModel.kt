package com.maksimowiczm.foodyou.feature.recipe.ui.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.FoodRepository
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.model.Recipe
import com.maksimowiczm.foodyou.feature.recipe.domain.Ingredient
import com.maksimowiczm.foodyou.feature.recipe.domain.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.feature.recipe.ui.MinimalIngredient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

internal class UpdateRecipeViewModel(
    private val foodRepository: FoodRepository,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    recipeId: FoodId.Recipe
) : ViewModel() {

    private val _eventBus = Channel<UpdateRecipeEvent>()
    val eventBus = _eventBus.receiveAsFlow()

    val recipe = foodRepository
        .observeFood(recipeId)
        .mapNotNull { it as? Recipe }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    fun observeIngredients(minimalIngredients: List<MinimalIngredient>): Flow<List<Ingredient>> {
        if (minimalIngredients.isEmpty()) {
            return flowOf(emptyList())
        }

        val flows = minimalIngredients.map { internalIngredient ->
            foodRepository
                .observeFood(internalIngredient.foodId)
                .filterNotNull()
                .map {
                    when (it) {
                        is Product -> Ingredient.Product(
                            uniqueId = it.id.toString(),
                            food = it,
                            measurement = internalIngredient.measurement
                        )

                        is Recipe -> Ingredient.Recipe(
                            uniqueId = it.id.toString(),
                            food = it,
                            measurement = internalIngredient.measurement
                        )
                    }
                }
        }

        return flows.combine { it.toList() }
    }

    fun onSave(name: String, servings: Int, ingredients: List<Ingredient>) = launch {
        val recipe = recipe.value ?: return@launch

        updateRecipeUseCase(
            recipeId = recipe.id,
            name = name,
            servings = servings,
            ingredients = ingredients
        )

        _eventBus.send(UpdateRecipeEvent.RecipeUpdated)
    }
}
