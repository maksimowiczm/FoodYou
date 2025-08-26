package com.maksimowiczm.foodyou.business.shared.application.query

import kotlinx.coroutines.flow.Flow

interface QueryBus {
    fun <R> dispatch(query: Query<R>): Flow<R>
}
