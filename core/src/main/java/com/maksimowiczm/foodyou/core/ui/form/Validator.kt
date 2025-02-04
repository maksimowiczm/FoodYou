package com.maksimowiczm.foodyou.core.ui.form

/**
 * Aliases for the [ValidationResult] types.
 */
object ValidatorScope {
    val success = ValidationResult.Success
    fun <E> failure(error: E) = ValidationResult.Failure(error)
}

fun interface Validator<T, E> {
    fun ValidatorScope.validate(value: T): ValidationResult
}

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Failure<E>(val error: E) : ValidationResult
}

fun <T : Any, E> allowNull(
    validator: (() -> Validator<T, E>)? = null
) = Validator<T?, E> {
    if (it == null) {
        success
    } else if (validator != null) {
        with(validator()) { validate(it) }
    } else {
        success
    }
}

fun <T : Any, E> notNull(
    onError: () -> E,
    validator: (() -> Validator<T, E>)? = null
) = Validator<T?, E> {
    if (it == null) {
        failure(onError())
    } else if (validator != null) {
        with(validator()) { validate(it) }
    } else {
        success
    }
}

fun <T : CharSequence, E> notEmpty(
    onError: () -> E,
    validator: (() -> Validator<T, E>)? = null
) = Validator<T, E> {
    if (it.isEmpty()) {
        failure(onError())
    } else if (validator != null) {
        with(validator()) { validate(it) }
    } else {
        success
    }
}

fun <E> nonNegative(
    onError: () -> E,
    validator: ((Float) -> Validator<Float, E>)? = null
) = Validator<Float, E> {
    if (it < 0) {
        failure(onError())
    } else if (validator != null) {
        with(validator(it)) { validate(it) }
    } else {
        success
    }
}
