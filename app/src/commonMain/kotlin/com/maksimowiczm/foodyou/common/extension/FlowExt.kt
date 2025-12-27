package com.maksimowiczm.foodyou.common.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

inline fun <reified T, R> Iterable<Flow<T>>.combine(
    crossinline transform: suspend (Array<T>) -> R
): Flow<R> = combine(this, transform)

inline fun <reified T> Iterable<Flow<T>>.combine(): Flow<List<T>> = combine { it.toList() }

@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6) {
        transform(it[0] as T1, it[1] as T2, it[2] as T3, it[3] as T4, it[4] as T5, it[5] as T6)
    }

@Suppress("UNCHECKED_CAST")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
        )
    }
