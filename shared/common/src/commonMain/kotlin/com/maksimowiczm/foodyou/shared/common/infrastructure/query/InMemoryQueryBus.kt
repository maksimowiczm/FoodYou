package com.maksimowiczm.foodyou.shared.common.infrastructure.query

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

internal class InMemoryQueryBus(queryHandlers: List<QueryHandler<Query, *>>) : QueryBus {
    private val queryHandlers: Map<KClass<Query>, QueryHandler<Query, *>> =
        queryHandlers.associateBy { it.queryType }

    override fun <R> dispatch(query: Query): Flow<R> {
        @Suppress("UNCHECKED_CAST")
        val handler =
            queryHandlers[query::class] as? QueryHandler<Query, R>
                ?: error("No handler found for query: ${query::class.simpleName}")

        return handler.handle(query)
    }
}
