package com.maksimowiczm.foodyou.core.ui.form

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T : Any?> emptyNullFormatter(value: T) = value?.toString() ?: ""

@Deprecated("dont do that")
@Composable
fun <T, E> rememberFormFieldWithTextFieldValue(
    initialValue: T,
    parser: Parser<T, E>,
    initialError: E? = null,
    initialDirty: Boolean = false,
    formatter: (T) -> String = { emptyNullFormatter(it) },
    initialTextFieldValue: TextFieldValue = TextFieldValue(
        text = formatter(initialValue),
        selection = TextRange(formatter(initialValue).length)
    ),
    validator: (() -> Validator<T, E>)? = null
): FormFieldWithTextFieldValue<T, E> {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    val formField = rememberFormField(
        initialValue = initialValue,
        initialError = initialError,
        initialDirty = initialDirty,
        parser = parser,
        validator = validator
    )

    val textFieldValue = rememberSaveable(
        stateSaver = TextFieldValue.Saver
    ) {
        mutableStateOf(initialTextFieldValue)
    }

    return remember {
        FormFieldWithTextFieldValue(
            initialTextFieldValue = textFieldValue,
            formField = formField,
            interactionSource = interactionSource,
            coroutineScope = coroutineScope,
            formatter = formatter
        )
    }
}

/**
 * A [FormField] that also holds a [TextFieldValue] for a text field.
 *
 * @param T the type of the value of the form field
 * @param E the type of the error of the form field
 * @param initialTextFieldValue the initial [TextFieldValue] of the text field
 * @param formField the form field
 * @param interactionSource the [MutableInteractionSource] of the text field
 * @param coroutineScope the [CoroutineScope] to launch coroutines
 * @param formatter the formatter to format the raw value to the text field value
 * @see FormField
 */
@Deprecated("dont do that")
@Stable
class FormFieldWithTextFieldValue<T, E>(
    initialTextFieldValue: MutableState<TextFieldValue>,
    private val formField: FormField<T, E>,
    val interactionSource: MutableInteractionSource,
    coroutineScope: CoroutineScope,
    private val formatter: (T) -> String
) : FormField<T, E> by formField {
    init {
        coroutineScope.launch {
            var wasFocused = false

            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is FocusInteraction.Focus -> wasFocused = true
                    is FocusInteraction.Unfocus -> if (wasFocused) {
                        formField.onValueChange(textFieldValue.text)
                    }
                }
            }
        }
    }

    var textFieldValue: TextFieldValue by initialTextFieldValue
        private set

    override fun touch() {
        formField.onValueChange(textFieldValue.text)
    }

    fun onValueChange(newValue: TextFieldValue, touch: Boolean = true) {
        if (newValue.text != textFieldValue.text) {
            formField.onValueChange(newValue.text, touch)
        }

        textFieldValue = newValue
    }

    override fun onRawValueChange(newValue: T, touch: Boolean) {
        formField.onRawValueChange(newValue, touch)

        val text = formatter(newValue)
        textFieldValue = TextFieldValue(
            text = text,
            selection = TextRange(text.length)
        )
    }
}
