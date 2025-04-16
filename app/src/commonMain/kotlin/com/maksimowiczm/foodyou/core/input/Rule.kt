package com.maksimowiczm.foodyou.core.input

fun interface Rule<E> {
    operator fun invoke(value: String): Sequence<E>
}
