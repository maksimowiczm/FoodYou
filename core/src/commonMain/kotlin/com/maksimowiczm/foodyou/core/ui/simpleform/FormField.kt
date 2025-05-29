package com.maksimowiczm.foodyou.core.ui.simpleform

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
class FormField<T, E>(
    val textFieldState: TextFieldState,
    valueState: MutableState<T>,
    errorState: MutableState<E?>
) {
    val value by valueState

    val error by errorState
}

@Composable
fun <T, E> rememberFormField(
    initialValue: T,
    parser: (String) -> ParseResult<T, E>,
    validator: (T) -> E? = { null },
    initialError: E? = null,
    textFieldState: TextFieldState = rememberTextFieldState()
): FormField<T, E> {
    var value = rememberSaveable { mutableStateOf(initialValue) }
    var error = rememberSaveable { mutableStateOf(initialError) }

    LaunchedEffect(textFieldState.text, parser, validator) {
        val text = textFieldState.text.toString()
        val result = parser(text)
        when (result) {
            is ParseResult.Success -> {
                value.value = result.value
                error.value = validator(result.value)
            }

            is ParseResult.Failure -> {
                error.value = result.error
            }
        }
    }

    return remember(textFieldState, value, error) {
        FormField(
            textFieldState = textFieldState,
            valueState = value,
            errorState = error
        )
    }
}
