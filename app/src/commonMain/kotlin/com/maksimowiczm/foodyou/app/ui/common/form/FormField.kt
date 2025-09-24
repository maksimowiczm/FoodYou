package com.maksimowiczm.foodyou.app.ui.common.form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
class FormField<T, E>(
    val textFieldState: TextFieldState,
    valueState: MutableState<T>,
    errorState: MutableState<E?>,
) {
    val value by valueState

    val error by errorState
}

/**
 * @param initialValue The initial value of the form field.
 * @param parser A function that parses the text input into a value of type [T].
 * @param validator A function that validates the parsed value and returns an error of type [E] if
 *   invalid, or null if valid.
 * @param initialError An optional initial error value, which can be null if there is no error.
 * @param textFieldState The state of the text field, which holds the current text input.
 * @param validateFirst If true, the validator will be called immediately after parsing the text
 *   input. If false, the value will be set first and then validated.
 */
@Composable
fun <T, E> rememberFormField(
    initialValue: T,
    parser: Parser<T, E>,
    validator: Validator<T, E> = defaultValidator(),
    initialError: E? = null,
    textFieldState: TextFieldState = rememberTextFieldState(),
    validateFirst: Boolean = false,
): FormField<T, E> {
    val valueState = rememberSaveable { mutableStateOf(initialValue) }
    val errorState = rememberSaveable { mutableStateOf(initialError) }

    var value by valueState
    var error by errorState

    LaunchedEffect(textFieldState.text, parser, validator) {
        val text = textFieldState.text.toString()

        when (val result = parser(text)) {
            is ParseResult.Success if (validateFirst) -> {
                error = validator(result.value)

                if (error == null) {
                    value = result.value
                }
            }

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
        FormField(textFieldState = textFieldState, valueState = valueState, errorState = errorState)
    }
}
