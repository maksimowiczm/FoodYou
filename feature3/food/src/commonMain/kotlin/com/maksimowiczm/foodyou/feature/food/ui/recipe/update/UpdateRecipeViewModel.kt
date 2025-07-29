package com.maksimowiczm.foodyou.feature.food.ui.recipe.update

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.database.food.Recipe
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.domain.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeFormState
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateRecipeViewModel(
    private val foodId: FoodId.Recipe,
    observeFoodUseCase: ObserveFoodUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase
) : RecipeViewModel(observeFoodUseCase) {

    val recipe = observeFoodUseCase.observe(foodId).stateIn(
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

        viewModelScope.launch {
            updateRecipeUseCase.update(
                id = foodId,
                name = form.name.value,
                servings = form.servings.value,
                note = form.note.value,
                isLiquid = form.isLiquid,
                ingredients = form.ingredients.map { it.intoPair() }
            )

            eventBus.send(UpdateRecipeEvent.Updated)
        }
    }

    private companion object {
        const val TAG = "UpdateRecipeViewModel"
    }
}
