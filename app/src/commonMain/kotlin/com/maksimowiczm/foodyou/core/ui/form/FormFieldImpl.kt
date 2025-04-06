package com.maksimowiczm.foodyou.core.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun <T, E> rememberFormField(
    initialValue: T,
    parser: Parser<T, E>,
    initialError: E? = null,
    initialDirty: Boolean = false,
    validator: (() -> Validator<T, E>)? = null
): FormFieldImpl<T, E> = rememberSaveable(
    saver = Saver(
        save = { field ->
            arrayListOf(
                field.value,
                field.error,
                field.dirty
            )
        },
        restore = {
            @Suppress("UNCHECKED_CAST")
            FormFieldImpl(
                initialFormFieldValue = FormFieldImpl.FormFieldValue(
                    value = it[0] as T,
                    error = it[1] as E
                ),
                initialDirty = it[2] as Boolean,
                parser = parser,
                validator = validator
            )
        }
    )
) {
    FormFieldImpl(
        initialFormFieldValue = FormFieldImpl.FormFieldValue(
            value = initialValue,
            error = initialError
        ),
        initialDirty = initialDirty,
        parser = parser,
        validator = validator
    )
}

@Stable
class FormFieldImpl<T, E>(
    initialFormFieldValue: FormFieldValue<T, E>,
    initialDirty: Boolean,
    private val parser: Parser<T, E>,
    private val validator: (() -> Validator<T, E>)? = null
) : FormField<T, E> {
    data class FormFieldValue<T, E>(val value: T, val error: E?)

    private var fieldValue by mutableStateOf(initialFormFieldValue)

    override val value: T by derivedStateOf { fieldValue.value }
    override val error: E? by derivedStateOf { fieldValue.error }

    override var dirty by mutableStateOf(initialDirty)
        private set

    override val isValid: Boolean by derivedStateOf {
        if (validator == null) {
            return@derivedStateOf error == null
        }

        when (validate(fieldValue.value)) {
            is ValidationResult.Failure<*> -> false
            ValidationResult.Success -> true
        }
    }

    override fun touch() {
        dirty = true
    }

    override fun onValueChange(newValue: String, touch: Boolean) {
        if (touch) {
            touch()
        }

        fieldValue = parseAndValidate(newValue)
    }

    override fun onRawValueChange(newValue: T, touch: Boolean) {
        if (touch) {
            touch()
        }

        fieldValue = when (val validated = validate(newValue)) {
            is ValidationResult.Failure<*> -> {
                @Suppress("UNCHECKED_CAST")
                FormFieldValue(
                    value = newValue,
                    error = validated.error as E
                )
            }

            is ValidationResult.Success -> {
                FormFieldValue(
                    value = newValue,
                    error = null
                )
            }
        }
    }

    private fun parseAndValidate(value: String): FormFieldValue<T, E> {
        val parsedValue = when (val result = parse(value)) {
            is ParserResult.Failure -> {
                return FormFieldValue(
                    value = fieldValue.value,
                    error = result.error
                )
            }

            is ParserResult.Success -> result.value
        }

        @Suppress("UNCHECKED_CAST")
        val validValue = when (val result = validate(parsedValue)) {
            is ValidationResult.Failure<*> -> {
                return FormFieldValue(
                    value = fieldValue.value,
                    error = result.error as E
                )
            }

            is ValidationResult.Success -> parsedValue
        }

        return FormFieldValue(
            value = validValue,
            error = null
        )
    }

    private fun parse(value: String) = with(ParserScope) {
        with(parser) {
            parse(value)
        }
    }

    private fun validate(value: T): ValidationResult {
        val validator = validator ?: return ValidationResult.Success

        return with(ValidatorScope) {
            with(validator()) {
                validate(value)
            }
        }
    }
}
