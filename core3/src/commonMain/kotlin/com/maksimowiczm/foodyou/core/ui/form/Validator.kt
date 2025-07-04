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
