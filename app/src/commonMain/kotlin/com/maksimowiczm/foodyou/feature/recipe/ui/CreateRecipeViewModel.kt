package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pro.respawn.kmmutils.inputforms.Form
import pro.respawn.kmmutils.inputforms.ValidationStrategy
import pro.respawn.kmmutils.inputforms.default.Rules

internal class CreateRecipeViewModel : ViewModel() {
    private val _state = MutableStateFlow<CreateRecipeFormState>(CreateRecipeFormState())
    val state = _state.asStateFlow()

    private val nameForm = Form(
        strategy = ValidationStrategy.LazyEval,
        Rules.NonEmpty
    )

    fun onNameChange(name: String) {
        _state.update {
            it.copy(
                name = nameForm(name)
            )
        }
    }

    private val servingsForm = Form(
        strategy = ValidationStrategy.LazyEval,
        Rules.DigitsOnly
    )

    fun onServingsChange(servings: String) {
        _state.update {
            it.copy(
                servings = servingsForm(servings)
            )
        }
    }

    fun onCreate() {
    }
}
