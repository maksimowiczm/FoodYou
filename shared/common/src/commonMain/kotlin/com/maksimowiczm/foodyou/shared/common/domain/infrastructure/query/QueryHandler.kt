package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query

import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

interface QueryHandler<in Q : Query, out R : Any?> {

    val queryType: KClass<@UnsafeVariance Q>

    fun handle(query: Q): Flow<R>
}
