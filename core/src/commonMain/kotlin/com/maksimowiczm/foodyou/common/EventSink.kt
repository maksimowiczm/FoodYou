package com.maksimowiczm.foodyou.common

fun interface EventSink<E> {
    suspend fun publish(stream: String, events: Iterable<E>)
}

/**
 * Combines this sink with [other], returning a sink that publishes to both.
 *
 * Both sinks receive every call to [EventSink.publish], in the order this sink then [other], for as
 * long as no sink throws first.
 */
operator fun <E> EventSink<E>.plus(other: EventSink<E>): EventSink<E> =
    CombinedEventSink(this, other)

private class CombinedEventSink<E>(vararg sinks: EventSink<E>) : EventSink<E> {
    private val sinks = sinks.toList()

    override suspend fun publish(stream: String, events: Iterable<E>) {
        sinks.forEach { it.publish(stream, events) }
    }
}
