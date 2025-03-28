package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.compose

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Stable
class FormField<T, E>(
    val textFieldState: TextFieldState,
    initialValue: T,
    initialError: E?,
    coroutineScope: CoroutineScope,
    private val parser: (String) -> ParseResult<T, E>,
    private val validator: (T) -> E?
) {
    var value by mutableStateOf(initialValue)
        private set

    var error by mutableStateOf<E?>(initialError)
        private set

    init {
        coroutineScope.launch {
            snapshotFlow { textFieldState.text }.collectLatest {
                val result = parser(it.toString())
                when (result) {
                    is ParseResult.Success -> {
                        value = result.value
                        error = validator(result.value)
                    }

                    is ParseResult.Failure -> {
                        error = result.error
                    }
                }
            }
        }
    }
}

sealed interface ParseResult<T, E> {
    data class Success<T, E>(val value: T) : ParseResult<T, E>
    data class Failure<T, E>(val error: E) : ParseResult<T, E>
}

@Composable
fun <T, E> rememberFormField(
    initialValue: T,
    parser: (String) -> ParseResult<T, E>,
    validator: (T) -> E? = { null },
    initialError: E? = null,
    textFieldState: TextFieldState = rememberTextFieldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): FormField<T, E> = rememberSaveable(
    textFieldState,
    initialValue,
    initialError,
    saver = Saver(
        save = {
            arrayListOf<Any?>(
                it.value,
                it.error
            )
        },
        restore = {
            @Suppress("UNCHECKED_CAST")
            FormField(
                textFieldState = textFieldState,
                initialValue = it[0] as T,
                initialError = it[1] as E?,
                coroutineScope = coroutineScope,
                parser = parser,
                validator = validator
            )
        }
    )
) {
    FormField(
        textFieldState = textFieldState,
        initialValue = initialValue,
        initialError = initialError,
        coroutineScope = coroutineScope,
        parser = parser,
        validator = validator
    )
}
