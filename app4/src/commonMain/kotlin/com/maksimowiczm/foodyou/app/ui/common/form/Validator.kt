package com.maksimowiczm.foodyou.app.ui.common.form

fun interface Validator<T, E> {
    /**
     * Validates the given value and returns an error if validation fails.
     *
     * @param value The value to validate.
     * @return An error of type [E] if validation fails, or null if validation is successful.
     */
    fun validate(value: T): E?

    operator fun invoke(value: T) = validate(value)
}

fun <T, E> defaultValidator(): Validator<T, E> = Validator { null }

fun <E> nonNegativeFloatValidator(
    onNegative: () -> E,
    onNull: () -> E? = { null },
): (Float?) -> E? = {
    when {
        it == null -> onNull()
        it < 0f -> onNegative()
        else -> null
    }
}

fun <E> positiveFloatValidator(
    onNotPositive: () -> E,
    onNull: () -> E? = { null },
): (Float?) -> E? = {
    when {
        it == null -> onNull()
        it <= 0f -> onNotPositive()
        else -> null
    }
}

fun <E> nonBlankStringValidator(onEmpty: () -> E): (String) -> E? = {
    if (it.isBlank()) {
        onEmpty()
    } else {
        null
    }
}

fun <E> numericStringValidator(
    onNotNumeric: () -> E,
    onNull: () -> E? = { null },
): (String?) -> E? = {
    when {
        it == null || it.isBlank() -> onNull()
        it.all { char -> char.isDigit() } -> null
        else -> onNotNumeric()
    }
}

fun <E> positiveIntValidator(onNotPositive: () -> E, onNull: () -> E? = { null }): (Int?) -> E? = {
    when {
        it == null -> onNull()
        it <= 0 -> onNotPositive()
        else -> null
    }
}

fun <E> nonNegativeIntValidator(onNegative: () -> E, onNull: () -> E? = { null }): (Int?) -> E? = {
    when {
        it == null -> onNull()
        it < 0 -> onNegative()
        else -> null
    }
}

fun <E> nonNegativeDoubleValidator(
    onNegative: () -> E,
    onNull: () -> E? = { null },
): (Double?) -> E? = {
    when {
        it == null -> onNull()
        it < 0.0 -> onNegative()
        else -> null
    }
}
