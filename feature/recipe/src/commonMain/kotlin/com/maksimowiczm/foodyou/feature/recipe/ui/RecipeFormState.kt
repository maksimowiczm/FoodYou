package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult
import com.maksimowiczm.foodyou.core.ui.simpleform.rememberFormField
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

internal enum class RecipeFormFieldError {
    Required,
    NotAInteger,
    NotPositive;

    @Composable
    fun stringResource(): String = when (this) {
        Required -> stringResource(Res.string.neutral_required)
        NotAInteger -> stringResource(Res.string.error_value_must_be_integer)
        NotPositive -> stringResource(Res.string.error_value_must_be_positive)
    }
}

@Composable
internal fun rememberRecipeFormState(
    initialName: String = "",
    initialServings: Int = 1,
    initialIngredients: List<MinimalIngredient> = emptyList()
): RecipeFormState {
    val nameState = rememberFormField(
        initialValue = initialName,
        parser = { ParseResult.Success(it) },
        validator = {
            if (it.isBlank()) {
                RecipeFormFieldError.Required
            } else {
                null
            }
        },
        textFieldState = rememberTextFieldState(initialName)
    )

    val servingsState = rememberFormField(
        initialValue = initialServings,
        parser = {
            if (it.isBlank()) {
                return@rememberFormField ParseResult.Failure(RecipeFormFieldError.Required)
            }

            it.toIntOrNull()
                ?.let { ParseResult.Success(it) }
                ?: ParseResult.Failure(RecipeFormFieldError.NotAInteger)
        },
        validator = {
            if (it <= 0) {
                RecipeFormFieldError.NotPositive
            } else {
                null
            }
        },
        textFieldState = rememberTextFieldState(initialServings.toString())
    )

    val ingredientsState = rememberSaveable(
        stateSaver = MinimalIngredient.ListSaver
    ) {
        mutableStateOf(initialIngredients)
    }

    val isModified = remember(initialName, initialName, initialServings) {
        derivedStateOf {
            initialName != nameState.value ||
                initialServings != servingsState.value ||
                initialIngredients != ingredientsState.value
        }
    }

    return remember(nameState, servingsState, ingredientsState, isModified) {
        RecipeFormState(
            nameState = nameState,
            servingsState = servingsState,
            ingredientsState = ingredientsState,
            isModifiedState = isModified
        )
    }
}

@Stable
internal class RecipeFormState(
    val nameState: FormField<String, RecipeFormFieldError>,
    val servingsState: FormField<Int, RecipeFormFieldError>,
    ingredientsState: MutableState<List<MinimalIngredient>>,
    isModifiedState: State<Boolean>
) {
    val isValid by derivedStateOf {
        nameState.error == null && servingsState.error == null
    }

    var ingredients by ingredientsState

    val isModified by isModifiedState
}
