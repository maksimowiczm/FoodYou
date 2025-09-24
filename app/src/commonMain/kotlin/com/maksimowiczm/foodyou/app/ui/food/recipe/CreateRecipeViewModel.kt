package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.result.consume
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.usecase.CreateRecipeUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class CreateRecipeViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase,
    private val dateProvider: DateProvider,
) : RecipeViewModel(observeFoodUseCase) {

    private val eventBus = Channel<CreateRecipeEvent>()
    val events = eventBus.receiveAsFlow()

    fun create(form: RecipeFormState) {
        if (!form.isValid) {
            return
        }

        viewModelScope.launch {
            createRecipeUseCase
                .create(
                    name = form.name.value,
                    servings = form.servings.value,
                    note = form.note.value,
                    isLiquid = form.isLiquid,
                    ingredients = form.ingredients.map { it.intoPair() },
                    history = FoodHistory.Created(dateProvider.nowInstant()),
                )
                .consume(
                    onSuccess = { eventBus.send(CreateRecipeEvent.Created(it)) },
                    onFailure = {
                        // Explode
                        error("Failed to create recipe")
                    },
                )
        }
    }
}
