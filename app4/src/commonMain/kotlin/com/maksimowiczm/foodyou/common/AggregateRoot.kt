package com.maksimowiczm.foodyou.common

import com.maksimowiczm.foodyou.common.event.DomainEvent

abstract class AggregateRoot {
    private val _events = mutableListOf<DomainEvent>()
    val events: List<DomainEvent>
        get() = _events.toList()

    protected fun raise(event: DomainEvent) {
        _events.add(event)
    }

    fun clearEvents(): Unit = _events.clear()
}
