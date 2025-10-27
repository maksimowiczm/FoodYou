package com.maksimowiczm.foodyou.app.ui.common.form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.*

@Stable
class FormField<T, E>(
    val textFieldState: TextFieldState = TextFieldState(),
    val parser: Parser<T, E>,
    val validator: Validator<T, E> = defaultValidator(),
) {
    private val parseResult by derivedStateOf { parser(textFieldState.text.toString()) }

    private val validationError by derivedStateOf {
        when (val res = parseResult) {
            is ParseResult.Success -> validator(res.value)
            is ParseResult.Failure -> null
        }
    }

    val error by derivedStateOf {
        when (val res = parseResult) {
            is ParseResult.Success -> validationError
            is ParseResult.Failure -> res.error
        }
    }

    val isValid by derivedStateOf { error == null }

    /**
     * The parsed value of the form field, or null if parsing failed. Value is present even if there
     * is a validation error.
     */
    val value by derivedStateOf {
        when (val res = parseResult) {
            is ParseResult.Success -> res.value
            is ParseResult.Failure -> null
        }
    }
}
