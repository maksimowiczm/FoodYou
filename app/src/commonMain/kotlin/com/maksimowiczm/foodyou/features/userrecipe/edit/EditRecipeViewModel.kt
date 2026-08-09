package com.maksimowiczm.foodyou.features.userrecipe.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.features.userrecipe.RecipeFormState
import com.maksimowiczm.foodyou.features.userrecipe.RecipeFormTransformer
import com.maksimowiczm.foodyou.features.userrecipe.RecipeFormUiState
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditRecipeViewModel(
    private val recipeService: UserRecipeService,
    private val recipeFormTransformer: RecipeFormTransformer,
    private val identity: UserRecipeIdentity,
) : ViewModel() {
    private val eventBus = Channel<EditRecipeEvent>()
    val uiEvents = eventBus.receiveAsFlow()

    val isLocked = MutableStateFlow(true)

    val recipe =
        recipeService
            .observe(identity)
            .onEach { isLocked.value = false }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

    fun save(form: RecipeFormState, state: RecipeFormUiState) {
        if (!isLocked.compareAndSet(expect = false, update = true)) return

        viewModelScope.launch {
            val (name, note, imageBytes, servings, components) =
                recipeFormTransformer.transform(form, state)

            recipeService.edit(
                identity = identity,
                name = name,
                note = note,
                imageBytes = imageBytes,
                servings = servings,
                components = components,
            )

            eventBus.send(EditRecipeEvent.Updated)
        }
    }
}
