package com.maksimowiczm.foodyou.core.input.dsl

import com.maksimowiczm.foodyou.core.input.Input
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@JvmName("inputString")
fun <E> input(value: String? = null): Input<E> = value.input()

@JvmName("inputStringExtension")
fun <E> String?.input(): Input<E> = takeIfValid()?.let(Input<E>::Valid) ?: Input<E>.Empty("")

/**
 * @return Whether this string is valid
 *
 * Examples:
 * - null -> false
 * - "null" -> false
 * - "" -> false
 * - "NULL" -> false
 * - "  " -> false
 */
@OptIn(ExperimentalContracts::class)
fun String?.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid != null)
    }
    return !isNullOrBlank() && !equals("null", true)
}

fun String?.takeIfValid(): String? = if (isValid()) this else null
