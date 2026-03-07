package com.maksimowiczm.foodyou.common.event

import kotlin.test.assertTrue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A test implementation of [EventBus] for unit testing event-driven components.
 *
 * This class provides a simple, in-memory event bus using Kotlin coroutines channels, designed
 * specifically for testing scenarios where you need to verify event publishing and collection
 * behavior.
 *
 * @param E The type of events that this event bus handles
 * @property channel The underlying channel for event transmission. Defaults to an unlimited
 *   capacity channel that never suspends on send operations.
 * @sample
 *
 * ```
 * @Test
 * fun `test event publishing`() = runTest {
 *     val eventBus = TestEventBus<UserEvent>()
 *
 *     eventBus.publish(UserEvent.LoggedIn("user123"))
 *     eventBus.close()
 *
 *     val events = eventBus.events.toList()
 *     assertEquals(1, events.size)
 * }
 * ```
 */
@OptIn(DelicateCoroutinesApi::class)
class ChannelEventBus<E>(val channel: Channel<E> = Channel(Channel.UNLIMITED)) : EventBus<E> {

    /**
     * A cold Flow that emits all events published to this event bus.
     *
     * Collectors will receive events published after they start collecting. The flow completes when
     * the underlying channel is closed.
     */
    override val events: Flow<E> = channel.receiveAsFlow()

    override suspend fun publish(event: E) {
        assertOpen()
        channel.send(event)
    }

    override suspend fun publish(events: List<E>) {
        events.forEach { channel.send(it) }
    }

    /**
     * Closes the underlying channel, preventing any further events from being published.
     *
     * Any ongoing collectors will complete after receiving all previously published events. Call
     * this method in test cleanup to properly release resources.
     *
     * @sample
     *
     * ```
     * val eventBus = TestEventBus<String>()
     * // ... use the event bus
     * eventBus.close()
     *
     * val events = eventBus.events.toList()
     * // ... assert the events
     *
     * ```
     */
    fun close(): Boolean {
        assertOpen()
        return channel.close()
    }

    private fun assertOpen() =
        assertTrue("Channel should not be closed") { !channel.isClosedForSend }
}

class ListEventBus<E> : EventBus<E> {
    private val _events = mutableListOf<E>()
    val publishedEvents: List<E>
        get() = _events.toList()

    override val events: Flow<E>
        get() = error("Not implemented")

    override suspend fun publish(event: E) {
        _events.add(event)
    }

    override suspend fun publish(events: List<E>) {
        _events.addAll(events)
    }
}
