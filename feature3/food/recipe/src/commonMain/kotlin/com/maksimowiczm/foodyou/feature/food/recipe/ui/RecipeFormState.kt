package com.maksimowiczm.foodyou.feature.food.recipe.ui

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.MinimalIngredient
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.form.intParser
import com.maksimowiczm.foodyou.shared.ui.form.nonBlankStringValidator
import com.maksimowiczm.foodyou.shared.ui.form.nullableStringParser
import com.maksimowiczm.foodyou.shared.ui.form.positiveIntValidator
import com.maksimowiczm.foodyou.shared.ui.form.rememberFormField
import com.maksimowiczm.foodyou.shared.ui.form.stringParser
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

internal enum class RecipeFormFieldError {
    Required,
    NotAInteger,
    NotPositive;

    @Composable
    fun stringResource(): String =
        when (this) {
            Required -> stringResource(Res.string.neutral_required)
            NotAInteger -> stringResource(Res.string.error_value_must_be_integer)
            NotPositive -> stringResource(Res.string.error_value_must_be_positive)
        }
}

@Composable
internal fun rememberRecipeFormState(
    initialName: String,
    initialServings: Int,
    initialNote: String?,
    initialIsLiquid: Boolean,
    initialIngredients: List<MinimalIngredient>,
): RecipeFormState {
    val name =
        rememberFormField(
            initialValue = initialName,
            parser = stringParser(),
            validator = nonBlankStringValidator(onEmpty = { RecipeFormFieldError.Required }),
            textFieldState = rememberTextFieldState(initialName),
        )

    val servings =
        rememberFormField(
            initialValue = initialServings,
            parser =
                intParser(
                    onNotANumber = { RecipeFormFieldError.NotAInteger },
                    onBlank = { RecipeFormFieldError.Required },
                ),
            validator = positiveIntValidator(onNotPositive = { RecipeFormFieldError.NotPositive }),
            textFieldState = rememberTextFieldState(initialServings.toString()),
        )

    val note =
        rememberFormField<String?, RecipeFormFieldError>(
            initialValue = initialNote,
            parser = nullableStringParser(),
            textFieldState = rememberTextFieldState(initialNote ?: ""),
        )

    val ingredientsState =
        rememberSaveable(stateSaver = MinimalIngredient.ListSaver) {
            mutableStateOf(initialIngredients)
        }

    val isLiquidState = rememberSaveable { mutableStateOf(initialIsLiquid) }

    val isModified =
        remember(
            initialName,
            initialServings,
            initialNote,
            initialIsLiquid,
            ingredientsState.value,
        ) {
            derivedStateOf {
                initialName != name.value ||
                    initialServings != servings.value ||
                    initialIngredients != ingredientsState.value ||
                    initialNote != note.value ||
                    initialIsLiquid != isLiquidState.value
            }
        }

    return remember(name, servings, note, isLiquidState, ingredientsState, isModified) {
        RecipeFormState(
            name = name,
            servings = servings,
            note = note,
            isLiquidState = isLiquidState,
            ingredientsState = ingredientsState,
            isModifiedState = isModified,
        )
    }
}

@Stable
internal class RecipeFormState(
    val name: FormField<String, RecipeFormFieldError>,
    val servings: FormField<Int, RecipeFormFieldError>,
    val note: FormField<String?, RecipeFormFieldError>,
    isLiquidState: MutableState<Boolean>,
    ingredientsState: MutableState<List<MinimalIngredient>>,
    isModifiedState: State<Boolean>,
) {
    val isValid by derivedStateOf {
        name.error == null && servings.error == null && ingredients.isNotEmpty()
    }

    var isLiquid by isLiquidState

    var ingredients by ingredientsState
        private set

    fun addIngredient(ingredient: MinimalIngredient) {
        ingredients = ingredients + ingredient
    }

    fun removeIngredient(ingredient: MinimalIngredient) {
        ingredients = ingredients - ingredient
    }

    fun updateIngredient(index: Int, newIngredient: MinimalIngredient) {
        if (index in ingredients.indices) {
            ingredients = ingredients.toMutableList().apply { this[index] = newIngredient }
        }
    }

    val isModified by isModifiedState
}
