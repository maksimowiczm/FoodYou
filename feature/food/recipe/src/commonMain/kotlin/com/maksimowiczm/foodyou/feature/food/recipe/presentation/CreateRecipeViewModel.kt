package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.ui.shared.extension.now
import com.maksimowiczm.foodyou.feature.food.recipe.ui.RecipeFormState
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.usecase.CreateRecipeUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.shared.common.FoodYouLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

internal class CreateRecipeViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase,
) : RecipeViewModel(observeFoodUseCase) {

    private val eventBus = Channel<CreateRecipeEvent>()
    val events = eventBus.receiveAsFlow()

    fun create(form: RecipeFormState) {
        if (!form.isValid) {
            FoodYouLogger.e(TAG) { "Attempted to create recipe with invalid form state: $form" }
        }

        viewModelScope.launch {
            createRecipeUseCase
                .create(
                    name = form.name.value,
                    servings = form.servings.value,
                    note = form.note.value,
                    isLiquid = form.isLiquid,
                    ingredients = form.ingredients.map { it.intoPair() },
                    history = FoodHistory.Created(LocalDateTime.now()),
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
