package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.command.UpdateRecipeCommand
import com.maksimowiczm.foodyou.business.food.application.command.UpdateRecipeError
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.feature.food.recipe.ui.RecipeFormState
import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UpdateRecipeViewModel(
    private val foodId: FoodId.Recipe,
    observeFoodUseCase: ObserveFoodUseCase,
    private val commandBus: CommandBus,
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
            FoodYouLogger.e(TAG) { "Form is not valid, cannot update recipe." }
            return
        }

        if (!form.isModified) {
            FoodYouLogger.d(TAG) { "Form is not modified, no need to update recipe." }
            viewModelScope.launch { eventBus.send(UpdateRecipeEvent.Updated) }
            return
        }

        viewModelScope.launch {
            commandBus
                .dispatch<Unit, UpdateRecipeError>(
                    UpdateRecipeCommand(
                        id = foodId,
                        name = form.name.value,
                        servings = form.servings.value,
                        note = form.note.value,
                        isLiquid = form.isLiquid,
                        ingredients = form.ingredients.map { it.intoPair() },
                    )
                )
                .consume(
                    onSuccess = { eventBus.send(UpdateRecipeEvent.Updated) },
                    onFailure = { FoodYouLogger.e(TAG) { "Failed to update recipe" } },
                )
        }
    }

    private companion object {
        const val TAG = "UpdateRecipeViewModel"
    }
}
