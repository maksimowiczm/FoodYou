package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query

import kotlinx.coroutines.flow.Flow

interface QueryBus {
    fun <R> dispatch(query: Query): Flow<R>
}
