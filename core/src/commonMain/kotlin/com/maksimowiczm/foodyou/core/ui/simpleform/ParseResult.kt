package com.maksimowiczm.foodyou.core.ui.simpleform

sealed interface ParseResult<T, E> {
    data class Success<T, E>(val value: T) : ParseResult<T, E>
    data class Failure<T, E>(val error: E) : ParseResult<T, E>
}
