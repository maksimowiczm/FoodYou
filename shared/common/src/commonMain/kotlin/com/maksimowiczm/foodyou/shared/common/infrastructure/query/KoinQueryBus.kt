package com.maksimowiczm.foodyou.shared.common.infrastructure.query

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named

internal class KoinQueryBus() : QueryBus, KoinComponent {
    override fun <R> dispatch(query: Query<R>): Flow<R> {
        val kclass = query::class.qualifiedName!!
        val handler = get(named(kclass)) as QueryHandler<Query<R>, R>
        return handler.handle(query)
    }
}
