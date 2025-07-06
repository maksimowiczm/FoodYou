package com.maksimowiczm.foodyou.core.ui.form

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
    onNull: () -> E? = { null }
): (Float?) -> E? = {
    when {
        it == null -> onNull()
        it < 0f -> onNegative()
        else -> null
    }
}

fun <E> positiveFloatValidator(
    onNotPositive: () -> E,
    onNull: () -> E? = { null }
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
