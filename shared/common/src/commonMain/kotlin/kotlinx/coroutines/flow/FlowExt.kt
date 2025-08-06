package kotlinx.coroutines.flow

inline fun <reified T, R> Iterable<Flow<T>>.combine(
    crossinline transform: suspend (Array<T>) -> R
): Flow<R> = combine(this, transform)

inline fun <reified T> Iterable<Flow<T>>.combine(): Flow<List<T>> = combine { it.toList() }

inline fun <T, R> Flow<Iterable<T>>.mapValues(
    crossinline transform: suspend (value: T) -> R
): Flow<List<R>> = this.map { it.map { transform(it) } }

inline fun <T, R> Flow<T?>.mapIfNotNull(crossinline transform: suspend (value: T) -> R?): Flow<R?> =
    this.map { value -> value?.let { transform(it) } }

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
inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R,
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
        )
    }
