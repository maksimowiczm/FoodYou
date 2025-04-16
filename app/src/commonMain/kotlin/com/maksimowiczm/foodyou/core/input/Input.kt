package com.maksimowiczm.foodyou.core.input

sealed interface Input<E> {
    val value: String

    val isValid: Boolean
        get() = this is Valid<E>

    val isInvalid: Boolean
        get() = this is Invalid<E>

    data class Invalid<E>(override val value: String, val errors: List<E>) : Input<E>

    data class Empty<E>(override val value: String = "") : Input<E>

    data class Valid<E>(override val value: String) : Input<E>
}
