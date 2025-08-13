package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.command.CreateRecipeCommand
import com.maksimowiczm.foodyou.business.food.application.command.CreateRecipeError
import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.feature.food.recipe.ui.RecipeFormState
import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.ui.ext.now
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

internal class CreateRecipeViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    private val commandBus: CommandBus,
) : RecipeViewModel(observeFoodUseCase) {

    private val eventBus = Channel<CreateRecipeEvent>()
    val events = eventBus.receiveAsFlow()

    fun create(form: RecipeFormState) {
        if (!form.isValid) {
            FoodYouLogger.e(TAG) { "Attempted to create recipe with invalid form state: $form" }
        }

        viewModelScope.launch {
            commandBus
                .dispatch<FoodId.Recipe, CreateRecipeError>(
                    CreateRecipeCommand(
                        name = form.name.value,
                        servings = form.servings.value,
                        note = form.note.value,
                        isLiquid = form.isLiquid,
                        ingredients = form.ingredients.map { it.intoPair() },
                        event = FoodEvent.Created(LocalDateTime.now()),
                    )
                )
                .consume(
                    onSuccess = { eventBus.send(CreateRecipeEvent.Created(it)) },
                    onFailure = { FoodYouLogger.e(TAG) { "Failed to create recipe" } },
                )
        }
    }

    private companion object {
        private const val TAG = "CreateRecipeViewModel"
    }
}
