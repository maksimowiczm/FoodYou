package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult
import com.maksimowiczm.foodyou.core.ui.simpleform.rememberFormField
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

enum class RecipeFormFieldError {
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
fun rememberRecipeFormState(initialName: String = "", initialServings: Int = 1): RecipeFormState {
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

    return remember(nameState, servingsState) {
        RecipeFormState(nameState, servingsState)
    }
}

@Stable
class RecipeFormState(
    val nameState: FormField<String, RecipeFormFieldError>,
    val servingsState: FormField<Int, RecipeFormFieldError>
) {
    val isValid by derivedStateOf {
        nameState.error == null && servingsState.error == null
    }
}
