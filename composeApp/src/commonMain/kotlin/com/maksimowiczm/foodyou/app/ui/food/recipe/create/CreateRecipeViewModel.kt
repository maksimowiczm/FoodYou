package com.maksimowiczm.foodyou.app.ui.food.recipe.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeFormState
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeFormTransformer
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeFormUiState
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
            val (name, note, imageBytes, servings, composition) =
                recipeFormTransformer.transform(form, state)

            val id =
                recipeService.create(
                    name = name,
                    note = note,
                    imageBytes = imageBytes,
                    servings = servings,
                    composition = composition,
                )

            eventBus.send(CreateRecipeEvent.Created(id))
        }
    }
}
