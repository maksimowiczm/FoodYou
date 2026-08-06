package com.maksimowiczm.foodyou.features.food.recipe.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.features.food.recipe.RecipeFormState
import com.maksimowiczm.foodyou.features.food.recipe.RecipeFormTransformer
import com.maksimowiczm.foodyou.features.food.recipe.RecipeFormUiState
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateRecipeViewModel(
    private val recipeService: UserRecipeService,
    private val recipeFormTransformer: RecipeFormTransformer,
) : ViewModel() {
    private val eventBus = Channel<CreateRecipeEvent>()
    val uiEvents = eventBus.receiveAsFlow()
    val isLocked = MutableStateFlow(value = false)

    fun create(form: RecipeFormState, state: RecipeFormUiState) {
        if (!isLocked.compareAndSet(expect = false, update = true)) return

        viewModelScope.launch {
            val (name, note, imageBytes, servings, components) =
                recipeFormTransformer.transform(form, state)

            val id =
                recipeService.create(
                    name = name,
                    note = note,
                    imageBytes = imageBytes,
                    servings = servings,
                    components = components,
                )

            eventBus.send(CreateRecipeEvent.Created(id))
        }
    }
}
