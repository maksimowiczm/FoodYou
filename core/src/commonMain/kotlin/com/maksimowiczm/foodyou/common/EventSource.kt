package com.maksimowiczm.foodyou.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Provides access to the events of a given stream, both as a one-off query and as an ongoing stream
 * of updates.
 *
 * @param E the event type this source provides
 */
interface EventSource<E> {
    suspend fun load(stream: String): Iterable<E>

    /**
     * Returns a [Flow] that emits the events associated with [stream], re-emitting whenever they
     * change.
     *
     * Each emission is the full set of events for [stream] at that point, not just what changed
     * since the previous emission.
     */
    fun observe(stream: String): Flow<Iterable<E>>
}

/** Loads the events of [stream] from this source, keeping only those of type [E]. */
suspend inline fun <reified E> EventSource<*>.load(stream: String) =
    load(stream).filterIsInstance<E>()

/**
 * Observes the events of [stream] from this source, keeping only those of type [E] in each
 * emission.
 */
inline fun <reified E> EventSource<*>.observe(stream: String) =
    observe(stream).map { it.filterIsInstance<E>() }
