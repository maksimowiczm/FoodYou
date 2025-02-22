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

fun <E> stringParser(onEmpty: () -> E) = Parser {
    if (it.isEmpty()) {
        failure(onEmpty())
    } else {
        success(it)
    }
}

fun <E> nullableStringParser(): Parser<String?, E> = Parser {
    if (it.isEmpty()) {
        success(null)
    } else {
        success(it)
    }
}

fun <E> nullableFloatParser(onNan: () -> E) = Parser {
    if (it.isEmpty()) {
        success(null)
    } else {
        try {
            success(it.toFloat())
        } catch (e: NumberFormatException) {
            failure(onNan())
        }
    }
}

fun <E> floatParser(onEmpty: () -> E, onNan: () -> E) = Parser {
    if (it.isEmpty()) {
        failure(onEmpty())
    } else {
        try {
            success(it.toFloat())
        } catch (e: NumberFormatException) {
            failure(onNan())
        }
    }
}

fun <E> nullableIntParser(onNan: () -> E) = Parser {
    if (it.isEmpty()) {
        success(null)
    } else {
        try {
            success(it.toInt())
        } catch (e: NumberFormatException) {
            failure(onNan())
        }
    }
}

fun <E> intParser(onEmpty: () -> E, onNan: () -> E) = Parser {
    if (it.isEmpty()) {
        failure(onEmpty())
    } else {
        try {
            success(it.toInt())
        } catch (e: NumberFormatException) {
            failure(onNan())
        }
    }
}
