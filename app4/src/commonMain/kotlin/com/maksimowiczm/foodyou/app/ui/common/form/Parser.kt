package com.maksimowiczm.foodyou.app.ui.common.form

sealed interface ParseResult<T, E> {
    data class Success<T, E>(val value: T) : ParseResult<T, E>

    data class Failure<T, E>(val error: E) : ParseResult<T, E>
}

fun interface Parser<T, E> {
    /**
     * Parses the given input string and returns a [ParseResult].
     *
     * @param input The input string to parse.
     * @return A [ParseResult] containing either a successful parsed value of type [T] or an error
     *   of type [E].
     */
    fun parse(input: String): ParseResult<T, E>

    operator fun invoke(input: String): ParseResult<T, E> = parse(input)
}

fun <E> nullableFloatParser(onNotANumber: () -> E, onNull: () -> E? = { null }): Parser<Float?, E> =
    Parser { input ->
        if (input.isBlank()) {
            onNull().let { error ->
                if (error != null) {
                    ParseResult.Failure(error)
                } else {
                    ParseResult.Success(null)
                }
            }
        } else {
            val value = input.toFloatOrNull()

            if (value == null) {
                ParseResult.Failure(onNotANumber())
            } else {
                ParseResult.Success(value)
            }
        }
    }

fun <E> nullableDoubleParser(
    onNotANumber: () -> E,
    onNull: () -> E? = { null },
): Parser<Double?, E> = Parser { input ->
    if (input.isBlank()) {
        onNull().let { error ->
            if (error != null) {
                ParseResult.Failure(error)
            } else {
                ParseResult.Success(null)
            }
        }
    } else {
        val value = input.toDoubleOrNull()

        if (value == null) {
            ParseResult.Failure(onNotANumber())
        } else {
            ParseResult.Success(value)
        }
    }
}

fun <E> intParser(onNotANumber: () -> E, onBlank: () -> E): Parser<Int, E> = Parser { input ->
    if (input.isBlank()) {
        ParseResult.Failure(onBlank())
    } else {
        val value = input.toIntOrNull()

        if (value == null) {
            ParseResult.Failure(onNotANumber())
        } else {
            ParseResult.Success(value)
        }
    }
}

fun <E> stringParser(): Parser<String, E> = Parser { ParseResult.Success(it) }

fun <E> nullableStringParser(): Parser<String?, E> = Parser { input ->
    if (input.isBlank()) {
        ParseResult.Success(null)
    } else {
        ParseResult.Success(input)
    }
}

fun <E> floatParser(onNotANumber: () -> E, onBlank: () -> E): Parser<Float, E> = Parser { input ->
    if (input.isBlank()) {
        ParseResult.Failure(onBlank())
    } else {
        val value = input.toFloatOrNull()

        if (value == null) {
            ParseResult.Failure(onNotANumber())
        } else {
            ParseResult.Success(value)
        }
    }
}

fun <E> doubleParser(onNotANumber: () -> E, onBlank: () -> E): Parser<Double, E> = Parser { input ->
    if (input.isBlank()) {
        ParseResult.Failure(onBlank())
    } else {
        val value = input.toDoubleOrNull()

        if (value == null) {
            ParseResult.Failure(onNotANumber())
        } else {
            ParseResult.Success(value)
        }
    }
}
