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
