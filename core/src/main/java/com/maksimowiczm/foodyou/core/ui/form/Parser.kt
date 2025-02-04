package com.maksimowiczm.foodyou.core.ui.form

/**
 * Aliases for the [ParserResult] types.
 */
object ParserScope {
    fun <T, E> success(value: T) = ParserResult.Success<T, E>(value)
    fun <T, E> failure(error: E) = ParserResult.Failure<T, E>(error)
}

fun interface Parser<T, E> {
    fun ParserScope.parse(value: String): ParserResult<T, E>
}

sealed interface ParserResult<T, E> {
    data class Success<T, E>(val value: T) : ParserResult<T, E>
    data class Failure<T, E>(val error: E) : ParserResult<T, E>
}

fun <E> nullableStringParser(
    onEmpty: ParserScope.() -> ParserResult<String?, E> = { success(null) }
): Parser<String?, E> = Parser {
    if (it.isEmpty()) {
        onEmpty()
    } else {
        success(it)
    }
}

fun <E> nullableFloatParser(
    onEmpty: ParserScope.() -> ParserResult<Float?, E> = { success(null) },
    onNan: () -> E
) = Parser {
    if (it.isEmpty()) {
        onEmpty()
    } else {
        try {
            success(it.toFloat())
        } catch (e: NumberFormatException) {
            failure(onNan())
        }
    }
}
