package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command

import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

interface CommandHandler<in C : Command, out R, out E> {
    val commandType: KClass<@UnsafeVariance C>

    suspend fun handle(command: C): Result<R, E>
}
