package com.maksimowiczm.foodyou.core.input

import pro.respawn.kmmutils.common.isValid

sealed interface Input<E> {
    val value: String

    val isValid: Boolean
        get() = this is Valid<E>

    val isEmptyValue: Boolean
        get() = !value.isValid()

    val isInvalid: Boolean
        get() = this is Invalid<E>

    val isValidOrEmpty: Boolean
        get() = this is Valid<E> || this is Empty<E>

    data class Invalid<E>(override val value: String, val errors: List<E>) : Input<E>

    data class Empty<E>(override val value: String = "") : Input<E>

    data class Valid<E>(override val value: String) : Input<E>
}
