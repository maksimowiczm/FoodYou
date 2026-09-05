package com.maksimowiczm.foodyou.userproduct.application

import com.maksimowiczm.foodyou.common.asEventSink
import com.maksimowiczm.foodyou.common.asHandler
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.observe
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductCommand
import com.maksimowiczm.foodyou.userproduct.domain.UserProductEvent
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userproduct.domain.toUserProduct
import com.maksimowiczm.foodyou.userproduct.domain.userProductDecider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProductService(
    private val eventStore: EventStore,
    eventBus: EventBus,
) {
    private val commandHandler = userProductDecider.asHandler(eventStore, eventBus.asEventSink())

    private fun stream(id: UserProductId): String = "UserProduct-${id.value}"

    suspend fun handle(id: UserProductId, command: UserProductCommand) {
        val _ = commandHandler(stream(id), command)
    }

    fun observe(id: UserProductId): Flow<UserProduct?> =
        eventStore.observe<UserProductEvent>(stream(id)).map { it.toUserProduct() }
}
