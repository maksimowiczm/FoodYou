package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query

import kotlinx.coroutines.flow.Flow

interface QueryHandler<Q : Query<R>, R> {
    fun handle(query: Q): Flow<R>
}
