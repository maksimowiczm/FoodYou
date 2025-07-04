package com.maksimowiczm.foodyou.core.ui.form

sealed interface ParseResult<T, E> {
    data class Success<T, E>(val value: T) : ParseResult<T, E>
    data class Failure<T, E>(val error: E) : ParseResult<T, E>
}

fun interface Parser<T, E> {
    /**
     * Parses the given input string and returns a [ParseResult].
     *
     * @param input The input string to parse.
     * @return A [ParseResult] containing either a successful parsed value of type [T] or an error of type [E].
     */
    fun parse(input: String): ParseResult<T, E>

    operator fun invoke(input: String): ParseResult<T, E> = parse(input)
}
