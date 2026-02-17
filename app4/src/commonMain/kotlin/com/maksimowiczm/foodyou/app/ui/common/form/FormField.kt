package com.maksimowiczm.foodyou.app.ui.common.form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.ValidationResult

@Stable
class FormField(
    val textFieldState: TextFieldState = TextFieldState(),
    private val defaultValue: String? = null,
    val validation: Validation<String?> = Validation {},
) {
    private val validationResult: ValidationResult<String?> by derivedStateOf {
        validation(textFieldState.text.takeIf { it.isNotBlank() }?.toString())
    }

    val error: String? by derivedStateOf { validationResult.errors.firstOrNull()?.message }

    val isModified: Boolean by derivedStateOf {
        textFieldState.text.takeIf { it.isNotBlank() } != defaultValue
    }
}

@Composable
fun rememberFormField(
    vararg keys: Any?,
    defaultValue: String? = null,
    validationBuilder: ValidationBuilder<String?>.() -> Unit,
): FormField {
    val textFieldState = rememberTextFieldState(defaultValue ?: "")
    val validation = remember(*keys, validationBuilder) { Validation { validationBuilder() } }
    return remember(*keys, defaultValue, textFieldState, validation) {
        FormField(textFieldState, defaultValue, validation)
    }
}

@Composable
fun rememberFormField(defaultValue: String? = null): FormField {
    val textFieldState = rememberTextFieldState(defaultValue ?: "")
    return remember(textFieldState, defaultValue) {
        FormField(textFieldState, defaultValue, Validation {})
    }
}
