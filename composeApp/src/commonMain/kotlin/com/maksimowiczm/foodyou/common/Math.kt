package com.maksimowiczm.foodyou.common

fun interface Plus<T> {
    operator fun plus(other: T): T
}

fun interface Times<out T> {
    operator fun times(scale: Number): T
}

fun interface Div<out T> {
    operator fun div(scale: Number): T
}
