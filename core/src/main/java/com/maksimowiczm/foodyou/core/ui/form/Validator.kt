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

inline fun <reified N, E> nonNegative(
    noinline onError: () -> E,
    noinline validator: ((N) -> Validator<N, E>)? = null
) where N : Number, N : Comparable<N> = min(zero<N>(), onError, validator)

inline fun <reified N : Number> zero() = when (N::class) {
    Byte::class -> 0.toByte() as N
    Short::class -> 0.toShort() as N
    Int::class -> 0 as N
    Long::class -> 0L as N
    Float::class -> 0f as N
    Double::class -> 0.0 as N
    else -> error("Unsupported number type")
}

/**
 * Validates if the value is greater than the [min] value. If the value is not greater than the
 * [min] value, the [onError] is returned.
 */
fun <E, T : Comparable<T>> min(
    min: T,
    onError: () -> E,
    validator: ((T) -> Validator<T, E>)? = null
) = Validator<T, E> {
    if (it < min) {
        failure(onError())
    } else if (validator != null) {
        with(validator(it)) { validate(it) }
    } else {
        success
    }
}

/**
 * Validates if the value is between the [min] and [max] values inclusive. If the value is not
 * between the [min] and [max] values, the [onMinError] or [onMaxError] is returned.
 */
fun <E, T : Comparable<T>> between(
    min: T,
    max: T,
    onMinError: () -> E,
    onMaxError: () -> E = onMinError,
    validator: ((T) -> Validator<T, E>)? = null
) = Validator<T, E> {
    if (it < min) {
        failure(onMinError())
    } else if (it > max) {
        failure(onMaxError())
    } else if (validator != null) {
        with(validator(it)) { validate(it) }
    } else {
        success
    }
}
