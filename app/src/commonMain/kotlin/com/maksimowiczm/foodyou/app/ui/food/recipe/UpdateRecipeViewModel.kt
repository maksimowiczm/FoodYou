package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.result.onError
import com.maksimowiczm.foodyou.common.result.onSuccess
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.entity.Recipe
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.UpdateRecipeUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateRecipeViewModel(
    private val foodId: FoodId.Recipe,
    observeFoodUseCase: ObserveFoodUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
) : RecipeViewModel(observeFoodUseCase) {

    val recipe =
        observeFoodUseCase
            .observe(foodId)
            .mapNotNull { it as? Recipe }
            .stateIn(
                scope = viewModelScope,
                initialValue = null,
                started = SharingStarted.WhileSubscribed(2_000),
            )

    private val eventBus = Channel<UpdateRecipeEvent>()
    val events = eventBus.receiveAsFlow()

    fun update(form: RecipeFormState) {
        if (!form.isValid) {
            return
        }

        if (!form.isModified) {
            viewModelScope.launch { eventBus.send(UpdateRecipeEvent.Updated) }
            return
        }

        viewModelScope.launch {
            updateRecipeUseCase
                .update(
                    id = foodId,
                    name = form.name.value,
                    servings = form.servings.value,
                    note = form.note.value,
                    isLiquid = form.isLiquid,
                    ingredients = form.ingredients.map { it.intoPair() },
                )
                .onSuccess { eventBus.send(UpdateRecipeEvent.Updated) }
                .onError {
                    // Explode
                    error("Failed to update recipe: $it")
                }
        }
    }
}
