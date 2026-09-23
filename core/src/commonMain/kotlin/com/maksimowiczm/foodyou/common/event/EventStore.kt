package com.maksimowiczm.foodyou.common.event

import com.maksimowiczm.foodyou.common.EventSink
import com.maksimowiczm.foodyou.common.EventSource

interface EventStore : EventSource<DomainEvent>, EventSink<DomainEvent>
