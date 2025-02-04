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
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun <T, E> rememberFormFieldWithTextFieldValue(
    initialTextFieldValue: TextFieldValue,
    initialValue: T,
    initialError: E? = null,
    initialDirty: Boolean = false,
    requireDirty: Boolean = true,
    parser: Parser<T, E>,
    validator: (() -> Validator<T, E>)? = null
): FormFieldWithTextFieldValue<T, E> {
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    val formField = rememberFormField(
        initialValue = initialValue,
        initialError = initialError,
        initialDirty = initialDirty,
        requireDirty = requireDirty,
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
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class FormFieldWithTextFieldValue<T, E>(
    initialTextFieldValue: MutableState<TextFieldValue>,
    private val formField: FormField<T, E>,
    val interactionSource: MutableInteractionSource,
    coroutineScope: CoroutineScope
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
        textFieldValue = newValue
        formField.onValueChange(newValue.text, touch)
    }
}
