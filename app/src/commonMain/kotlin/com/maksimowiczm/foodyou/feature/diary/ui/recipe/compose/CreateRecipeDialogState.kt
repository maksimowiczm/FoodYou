package com.maksimowiczm.foodyou.feature.diary.ui.recipe.compose

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.ui.simpleform.FormField
import com.maksimowiczm.foodyou.ui.simpleform.ParseResult
import com.maksimowiczm.foodyou.ui.simpleform.rememberFormField

// Can't use Unit for an error because it must be parcelable
@Stable
class CreateRecipeDialogState(
    val nameTextFieldState: FormField<String, Byte>,
    val servingsTextFieldState: FormField<Int?, Byte>
)

@Composable
fun rememberCreateRecipeDialogState(
    nameTextFieldState: TextFieldState = rememberTextFieldState(),
    servingsTextFieldState: TextFieldState = rememberTextFieldState()
): CreateRecipeDialogState {
    val nameField = rememberFormField<String, Byte>(
        initialValue = "",
        parser = { ParseResult.Success(it) },
        textFieldState = nameTextFieldState,
        validator = { str ->
            when {
                str.isBlank() -> 0
                else -> null
            }
        }
    )

    val servingsField = rememberFormField<Int?, Byte>(
        initialValue = 1,
        parser = { str ->
            if (str.isBlank()) {
                return@rememberFormField ParseResult.Success(null)
            }

            val f = str.toIntOrNull()

            when (f) {
                null -> ParseResult.Failure(0)
                else if (f <= 0 || f > 9999) -> ParseResult.Failure(0)
                else -> ParseResult.Success(f)
            }
        },
        textFieldState = servingsTextFieldState
    )

    return remember(nameField, servingsField) {
        CreateRecipeDialogState(
            nameTextFieldState = nameField,
            servingsTextFieldState = servingsField
        )
    }
}
