package com.maksimowiczm.foodyou.core.ui.form

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
import androidx.compose.runtime.setValue

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
    parser: Parser<T, E>,
    validator: Validator<T, E> = defaultValidator(),
    initialError: E? = null,
    textFieldState: TextFieldState = rememberTextFieldState()
): FormField<T, E> {
    val valueState = rememberSaveable { mutableStateOf(initialValue) }
    val errorState = rememberSaveable { mutableStateOf(initialError) }

    var value by valueState
    var error by errorState

    LaunchedEffect(textFieldState.text, parser, validator) {
        val text = textFieldState.text.toString()

        when (val result = parser(text)) {
            is ParseResult.Success -> {
                value = result.value
                error = validator(value)
            }

            is ParseResult.Failure -> {
                error = result.error
            }
        }
    }

    return remember(textFieldState, value, error) {
        FormField(
            textFieldState = textFieldState,
            valueState = valueState,
            errorState = errorState
        )
    }
}
