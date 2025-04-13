package com.maksimowiczm.foodyou.core.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline fun <reified T, R> Iterable<Flow<T>>.combine(
    crossinline transform: suspend (Array<T>) -> R
): Flow<R> = combine(this, transform)

inline fun <T, R> Flow<Iterable<T>>.mapValues(
    crossinline transform: suspend (value: T) -> R
): Flow<List<R>> = this.map { it.map { transform(it) } }
