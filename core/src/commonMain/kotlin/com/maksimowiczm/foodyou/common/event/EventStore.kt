package com.maksimowiczm.foodyou.common.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface EventStore {
    suspend fun load(stream: String): Iterable<DomainEvent>

    fun observe(stream: String): Flow<Iterable<DomainEvent>>

    suspend fun append(stream: String, event: Iterable<DomainEvent>)
}

suspend inline fun <reified E : DomainEvent> EventStore.load(stream: String): Iterable<E> =
    load(stream).filterIsInstance<E>()

inline fun <reified E : DomainEvent> EventStore.observe(stream: String): Flow<Iterable<E>> =
    observe(stream).map { it.filterIsInstance<E>() }
