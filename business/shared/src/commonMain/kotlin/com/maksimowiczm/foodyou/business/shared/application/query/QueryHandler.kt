package com.maksimowiczm.foodyou.business.shared.application.query

import kotlinx.coroutines.flow.Flow

interface QueryHandler<Q : Query<R>, R> {
    fun handle(query: Q): Flow<R>
}
