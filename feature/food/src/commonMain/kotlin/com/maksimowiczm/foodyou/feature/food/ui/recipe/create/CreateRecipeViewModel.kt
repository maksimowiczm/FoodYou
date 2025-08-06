package com.maksimowiczm.foodyou.feature.food.ui.recipe.create

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.feature.food.domain.CreateRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.FoodEvent
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeFormState
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

internal class CreateRecipeViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    private val createRecipeUseCase: CreateRecipeUseCase
) : RecipeViewModel(observeFoodUseCase) {

    private val eventBus = Channel<CreateRecipeEvent>()
    val events = eventBus.receiveAsFlow()

    fun create(form: RecipeFormState) {
        if (!form.isValid) {
            Logger.w(TAG) { "Attempted to create recipe with invalid form state: $form" }
        }

        viewModelScope.launch {
            val id = createRecipeUseCase.create(
                name = form.name.value,
                servings = form.servings.value,
                note = form.note.value,
                isLiquid = form.isLiquid,
                ingredients = form.ingredients.map { it.intoPair() },
                event = FoodEvent.Created(LocalDateTime.now())
            )

            eventBus.send(CreateRecipeEvent.Created(id))
        }
    }

    private companion object {
        private const val TAG = "CreateRecipeViewModel"
    }
}
