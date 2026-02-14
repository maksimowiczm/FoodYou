package com.maksimowiczm.foodyou.app.ui.common.form

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

@Stable
class FormField2(
    val textFieldState: TextFieldState,
    private val defaultValue: String?,
    val validation: Validation<String?>,
) {
    private val validationResult: ValidationResult<String?> by derivedStateOf {
        validation(textFieldState.text.takeIf { it.isNotBlank() }?.toString())
    }

    val error: String? by derivedStateOf { validationResult.errors.firstOrNull()?.message }

    val isModified: Boolean by derivedStateOf { textFieldState.text != defaultValue }
}

@Composable
fun rememberFormField2(
    vararg key: Any?,
    defaultValue: String? = null,
    validationBuilder: ComposeValidationBuilder<String?>.() -> Unit,
): FormField2 {
    val textFieldState = rememberTextFieldState()
    val validation = rememberValidation(*key, validationBuilder = validationBuilder)
    return remember(*key, textFieldState, defaultValue, validation) {
        FormField2(textFieldState, defaultValue, validation)
    }
}

@Composable
fun rememberFormField2(defaultValue: String? = null): FormField2 {
    val textFieldState = rememberTextFieldState()
    return remember(textFieldState) { FormField2(textFieldState, defaultValue, Validation {}) }
}

// We have to support compose resources in validation builder. We could use @Composable lambda, but
// this requires reconstructing Validation every recomposition. Perhaps it could be possible to use
// internal compose resources API to create workaround, but this also works.
class ComposeValidationBuilder<T> : ValidationBuilder<T>() {
    fun stringResource(resource: StringResource): String =
        runBlocking(Dispatchers.Main.immediate) { getString(resource) }
}

@Composable
fun <T> rememberValidation(
    vararg key: Any?,
    validationBuilder: ComposeValidationBuilder<T>.() -> Unit,
): Validation<T> =
    remember(key, validationBuilder) {
        val builder = ComposeValidationBuilder<T>()
        validationBuilder(builder)
        builder.build()
    }
